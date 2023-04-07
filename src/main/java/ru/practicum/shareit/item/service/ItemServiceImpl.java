package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDetails;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.repository.CommentRepository;
import ru.practicum.shareit.repository.ItemRepository;
import ru.practicum.shareit.repository.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemStorage;
    private final UserRepository userStorage;
    private final BookingRepository bookingStorage;
    private final CommentRepository commentStorage;

    @Autowired
    public ItemServiceImpl(ItemRepository itemStorage, UserRepository userStorage, BookingRepository bookingStorage, CommentRepository commentStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.bookingStorage = bookingStorage;
        this.commentStorage = commentStorage;
    }

    @Override
    public Collection<ItemAnswerDto> getAll(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID =  " + userId + " не найден");
        }
        List<Item> items = itemStorage.findByOwner_Id(userId);

        return getItemsAnswers(items);
    }

    @Transactional(readOnly = true)
    private Collection<ItemAnswerDto> getItemsAnswers(List<Item> items) {
        Map<Long, BookingDetails> lastBooking = new HashMap<>();
        Map<Long, BookingDetails> nextBooking = new HashMap<>();
        Map<Long, List<Comment>> comments = new HashMap<>();

        Map<Item, List<Booking>> approvedBookings =
                bookingStorage.findApprovedForItems(items)
                        .stream()
                        .collect(groupingBy(Booking::getItem, toList()));
        Map<Item, List<Comment>> commentsByItems =
                commentStorage.findCommentsForItems(items)
                        .stream()
                        .collect(groupingBy(Comment::getItem, toList()));

        items.forEach(key -> {
                    if (!approvedBookings.isEmpty()) {
                        if (approvedBookings.containsKey(key)) {
                            approvedBookings.get(key).stream()
                                    .filter(b -> b.getItem().getId().equals(key.getId()))
                                    .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                                    .findFirst()
                                    .map(BookingMapper::toBookingDetails)
                                    .ifPresent(b -> lastBooking.put(key.getId(), b));
                            approvedBookings.get(key).stream()
                                    .filter(b -> b.getItem().getId().equals(key.getId()))
                                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                                    .reduce((first, second) -> second)
                                    .map(BookingMapper::toBookingDetails)
                                    .ifPresent(b -> nextBooking.put(key.getId(), b));
                        }
                    }
                    comments.put(key.getId(), commentsByItems.isEmpty() ? Collections.emptyList() : commentsByItems.get(key));
                }
        );

        return items.stream()
                .map(item -> ItemMapper.toAnswerItemDto(
                        item,
                        lastBooking.size() == 0 ? null : lastBooking.get(item.getId()),
                        nextBooking.size() == 0 ? null : nextBooking.get(item.getId()),
                        comments.get(item.getId())
                                .stream()
                                .map(CommentMapper::toCommentDto)
                                .collect(toList())))
                .collect(toList());
    }

    @Override
    public ItemAnswerDto getItemByID(Long itemId, Long userId) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID = " + itemId + " не найдена"));

        ItemAnswerDto nededItem = getItemsAnswers(itemStorage.findByIdAndOwner_Id(itemId, userId)).stream()
                .findFirst()
                .orElse(null);

        List<CommentDto> comments = commentStorage.findAllByItem_Id(item.getId())
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList());

        if (nededItem == null) {
            return ItemMapper.toAnswerItemDto(item, null, null, comments);
        } else {
            return ItemMapper.toAnswerItemDto(item, nededItem.getLastBooking(), nededItem.getNextBooking(), comments);
        }
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userID) {
        validationItemDto(itemDto);
        Optional<User> user = userStorage.findById(userID);
        if (user.isEmpty()) {
            throw new NotFoundException("Не найден пользователь по ID = " + userID);
        }
        itemDto.setOwner(user.get());
        Item item = ItemMapper.toItem(itemDto);
        itemDto = ItemMapper.toItemDto(itemStorage.save(item));
        return itemDto;
    }

    @Override
    public ItemDto update(Long itemID, Long userID, ItemDto itemDto) {
        Optional<User> user = userStorage.findById(userID);
        Optional<Item> item = itemStorage.findById(itemID);
        if (user.isEmpty() || item.isEmpty()) {
            throw new NotFoundException("Не удалось найти вещь для обновления");
        }
        if (!item.get().getOwner().getId().equals(userID)) {
            throw new NotFoundException("Нельзя обновлять не свои вещи");
        }

        if (itemDto.getName() != null) {
            item.get().setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.get().setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.get().setAvailable(itemDto.getAvailable());
        }
        itemStorage.save(item.get());

        return ItemMapper.toItemDto(itemStorage.findById(itemID).get());
    }

    @Override
    public Collection<Item> searchItem(String query) {
        if (query.isEmpty()) {
            return Collections.emptyList();
        }
        return itemStorage.searchItem(query);
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        validationComment(commentDto, itemId, userId);
        Optional<User> fUser = userStorage.findById(userId);
        Optional<Item> fItem = itemStorage.findById(itemId);
        if (fUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (fItem.isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        }

        Comment newComment = new Comment();
        newComment.setText(commentDto.getText());
        newComment.setAuthor(fUser.get());
        newComment.setCreated(LocalDateTime.now());
        newComment.setItem(fItem.get());
        return CommentMapper.toCommentDto(commentStorage.save(newComment));
    }

    @Override
    public void validationItemDto(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new IncorrectParameterException("available");
        }
    }

    @Override
    public void validationComment(CommentDto commentDto, Long itemId, Long userId) {
        if (commentDto.getText().isBlank()) {
            throw new IncorrectParameterException("текст комментария не может быть пустым");
        }
        if (!bookingStorage.existsBookingByBooker_IdAndItem_IdAndStatusAndStartBefore(userId, itemId, BookingStatus.APPROVED,
                LocalDateTime.now())) {
            throw new IncorrectParameterException("Пользоваель с ID =" + userId + " не бронировал вещь с ID =" + itemId);
        }

    }
}

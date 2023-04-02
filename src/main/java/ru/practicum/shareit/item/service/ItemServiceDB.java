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
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceDB {
    private final ItemRepository itemStorage;
    private final UserRepository userStorage;
    private final BookingRepository bookingStorage;
    private final CommentRepository commentStorage;
    private static final Sort SORT_DESC = Sort.by(Sort.Direction.DESC, "end");
    private static final Sort SORT_ASC = Sort.by(Sort.Direction.ASC, "start");


    @Autowired
    public ItemServiceDB(ItemRepository itemStorage, UserRepository userStorage, BookingRepository bookingStorage, CommentRepository commentStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.bookingStorage = bookingStorage;
        this.commentStorage = commentStorage;
    }

    public Collection<ItemAnswerDto> getAll(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID =  " + userId + " не найден");
        }

        List<Item> items = itemStorage.findByOwner_Id(userId);
        List<Long> itemsId = items
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Comment> allComments = commentStorage.findAllByItemsId(itemsId);

        List<Booking> allLastBookings = bookingStorage
                .findFirstByItem_IdInAndItem_Owner_IdAndStartIsBefore(
                        itemsId,
                        userId,
                        LocalDateTime.now(),
                        SORT_DESC);

        List<Booking> allNextBookings = bookingStorage
                .findFirstByItem_IdInAndItem_Owner_IdAndStartIsAfterAndStatusIsNot(
                        itemsId,
                        userId,
                        LocalDateTime.now(),
                        BookingStatus.REJECTED,
                        SORT_ASC);

        Map<Long, BookingDetails> lastBooking = new HashMap<>();
        Map<Long, Booking> nextBooking = new HashMap<>();
        Map<Long, List<Comment>> comments = new HashMap<>();


        itemsId.forEach(key -> {
            allLastBookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(key))
                    .findFirst()
                    .map(BookingMapper::toBookingDetails)
                    .ifPresent(booking -> lastBooking.put(key, booking));
            allNextBookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(key))
                    .findFirst()
                    .ifPresent(booking -> nextBooking.put(key, booking));
            List<Comment> valueOfComments = allComments.stream()
                    .filter(comment -> Objects.equals(comment.getAuthor().getId(), key) && key != null)
                    .collect(Collectors.toList());
            comments.put(key, valueOfComments);
        });


        return items.stream()
                .map(item -> ItemMapper.toAnswerItemDto(
                        item,
                        lastBooking.size() == 0 ? null : lastBooking.get(item.getId()),
                        nextBooking.size() == 0 ? null : BookingMapper.toBookingDetails(nextBooking.get(item.getId())),
                        comments.get(item.getId())
                                .stream()
                                .map(CommentMapper::toCommentDto)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());

//        if (userId != null) {
//            List<ItemDto> items = itemStorage.findALlByOwner(userStorage.findById(userId).get()).stream()
//                    .sorted(Comparator.comparing(Item::getId))
//                    .map(this::itemToDto)
//                    .collect(Collectors.toList());
//            setBookingDetails(items);
//            return items;
//        }
//
//        List<ItemDto> items = itemStorage.findAll().stream()
//                .map(this::itemToDto)
//                .collect(Collectors.toList());
//        setBookingDetails(items);
//
//        return items;

    }

//    private ItemDto itemToDto(Item item) {
//        return ItemMapper.toItemDto(item);
//    }
//
//    private void setBookingDetails(List<ItemDto> items) {
//        List<Long> itemsId = items.stream()
//                .map(ItemDto::getId)
//                .collect(Collectors.toList());
//
//        List<Comment> commentList = commentStorage.findAllByItemsId(itemsId);
//        List<BookingDetails> lastBookings = bookingStorage.getLastBookings(itemsId, LocalDateTime.now());
//        List<BookingDetails> nextBookings = bookingStorage.getNextBookings(itemsId, LocalDateTime.now());
//
//        Map<Long, List<Comment>> comments = new HashMap<>();
//        itemsId.forEach(key -> {
//            List<Comment> commentsValue = commentList.stream()
//                    .filter(comment -> Objects.equals(comment.getAuthor().getId(), key))
//                    .collect(Collectors.toList());
//            comments.put(key, commentsValue);
//        });
//        if (!lastBookings.isEmpty()) {
//            for (int i = 0; i < lastBookings.size(); i++) {
//                items.get(i).setLastBooking(lastBookings.get(i));
//            }
//        }
//        if (!nextBookings.isEmpty()) {
//            for (int i = 0; i < nextBookings.size(); i++) {
//                items.get(i).setNextBooking(nextBookings.get(i));
//            }
//        }
//    }

    public ItemAnswerDto getItemByID(Long itemID, Long userId) {
        Item item = itemStorage.findById(itemID)
                .orElseThrow(() -> new NotFoundException("Вещь с ID = " + itemID + " не найдена"));
        LocalDateTime now = LocalDateTime.now();

        List<CommentDto> comments = commentStorage.findAllByItem_Id(item.getId())
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        BookingDetails lastBooking = BookingMapper.toBookingDetails(bookingStorage.findFirstByItem_IdAndItem_Owner_IdAndStartIsBefore(
                        itemID,
                        userId,
                        now,
                        SORT_DESC));

        BookingDetails nextBooking = BookingMapper.toBookingDetails(bookingStorage.findFirstByItem_IdAndItem_Owner_IdAndStartIsAfterAndStatusIsNot(
                        itemID,
                        userId,
                        now,
                        BookingStatus.REJECTED,
                        SORT_ASC));

        return ItemMapper.toAnswerItemDto(item, lastBooking, nextBooking, comments);
    }

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

    public Collection<Item> searchItem(String query) {
        if (query.isEmpty()) {
            return Collections.emptyList();
        }
        return itemStorage.searchItem(query);
    }

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

    public void validationItemDto(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new IncorrectParameterException("available");
        }
    }

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

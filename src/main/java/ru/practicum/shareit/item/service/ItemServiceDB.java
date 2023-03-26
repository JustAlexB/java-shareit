package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.BookingDetails;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceDB {
    private final ItemRepository itemStorage;
    private final UserRepository userStorage;
    private final BookingRepository bookingStorage;
    private final CommentRepository commentStorage;

    @Autowired
    public ItemServiceDB(ItemRepository itemStorage, UserRepository userStorage, BookingRepository bookingStorage, CommentRepository commentStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.bookingStorage = bookingStorage;
        this.commentStorage = commentStorage;
    }

    public Collection<ItemDto> getAll(Integer userID) {
        if (userID != null) {
            List<ItemDto> items = itemStorage.findALlByOwner(userStorage.findById(userID).get()).stream()
                    .sorted(Comparator.comparing(Item::getId))
                    .map(this::ItemToDto)
                    .collect(Collectors.toList());
            setBookingDetails(items);
            return items;
        }

        List<ItemDto> items =  itemStorage.findAll().stream()
                .map(this::ItemToDto)
                .collect(Collectors.toList());
        setBookingDetails(items);

        return items;
    }

    private ItemDto ItemToDto(Item item) {
        return ItemMapper.toItemDto(item);
    }

    private void setBookingDetails(List<ItemDto> items) {
        List<Integer> itemsId = items.stream()
                .map(ItemDto::getId)
                .collect(Collectors.toList());

        List<BookingDetails> lastBookings = bookingStorage.getLastBookings(itemsId, LocalDateTime.now());
        List<BookingDetails> nextBookings = bookingStorage.getNextBookings(itemsId, LocalDateTime.now());

        if (!lastBookings.isEmpty()) {
            for (int i = 0; i < lastBookings.size(); i++) {
                items.get(i).setLastBooking(lastBookings.get(i));
            }
        }

        if (!nextBookings.isEmpty()) {
            for (int i = 0; i < nextBookings.size(); i++) {
                items.get(i).setNextBooking(nextBookings.get(i));
            }
        }
    }

    public ItemDto getItemByID(Integer itemID, Integer userId) {
        Item fItem = itemStorage.findById(itemID)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с ID = " + itemID));
        ItemDto itemDto = ItemMapper.toItemDto(fItem);

        if(fItem.getOwner().getId() == userId) {
            BookingDetails lastBooking = bookingStorage.getLastBooking(itemID, LocalDateTime.now());
            itemDto.setLastBooking(lastBooking);
            BookingDetails nextBooking = bookingStorage.getNextBooking(itemID, LocalDateTime.now());
            itemDto.setNextBooking(nextBooking);
        }

        return itemDto;
    }

    public ItemDto create(ItemDto itemDto, Integer userID) {
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

    public ItemDto update(Integer itemID, Integer userID, ItemDto itemDto) {
        Optional<User> user = userStorage.findById(userID);
        Optional<Item> item = itemStorage.findById(itemID);
        if (user.isEmpty() || item.isEmpty()) {
            throw new NotFoundException("Не удалось найти вещь для обновления");
        }
        if (userID != item.get().getOwner().getId()) {
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
    public CommentDto addComment(CommentDto commentDto, Integer itemId, Integer userId){
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
        newComment.setAuthorName(fUser.get().getName());
        newComment.setCreated(LocalDateTime.now());
        newComment.setItem(fItem.get());
        return CommentMapper.toCommentDto(commentStorage.save(newComment));
    }

    public void validationItemDto(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new IncorrectParameterException("available");
        }
    }

    public void validationComment(CommentDto commentDto, Integer itemId, Integer userId) {
        if (commentDto.getText().isBlank()) {
            throw new IncorrectParameterException("текст комментария не может быть пустым");
        }
        if (!bookingStorage.existsBookingByBooker_IdAndItem_IdAndStatusAndStartBefore(userId, itemId, BookingStatus.APPROVED,
                LocalDateTime.now())) {
            throw new IncorrectParameterException("Пользоваель с ID =" + userId + " не бронировал вещь с ID ="  + itemId);
        }

    }
}

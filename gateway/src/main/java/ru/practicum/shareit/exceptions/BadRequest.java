package ru.practicum.shareit.exceptions;

public class BadRequest extends RuntimeException {

    public BadRequest(final String message) {
        super(message);
    }
}
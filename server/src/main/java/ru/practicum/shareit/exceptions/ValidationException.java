package ru.practicum.shareit.exceptions;

public class ValidationException extends RuntimeException {

    private Object element;

    public ValidationException(String message, Object element) {
        super(message);
        this.element = element;
    }

}

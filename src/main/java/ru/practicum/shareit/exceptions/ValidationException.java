package ru.practicum.shareit.exceptions;

public class ValidationException extends RuntimeException {

    private Object element;

    public ValidationException() {
    }

    public ValidationException(String message, Object element) {
        super(message);
        this.element = element;
    }

    public ValidationException(String message, Object element, Throwable cause) {
        super(message, cause);
        this.element = element;
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public Object getElement() {
        return element;
    }
}

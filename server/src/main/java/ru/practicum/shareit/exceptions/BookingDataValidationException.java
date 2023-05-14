package ru.practicum.shareit.exceptions;

public class BookingDataValidationException extends RuntimeException {
    private final String parameter;

    public BookingDataValidationException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}

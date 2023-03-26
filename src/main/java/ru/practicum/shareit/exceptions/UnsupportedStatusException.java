package ru.practicum.shareit.exceptions;

public class UnsupportedStatusException extends RuntimeException {
    private final String parameter;

    public UnsupportedStatusException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}

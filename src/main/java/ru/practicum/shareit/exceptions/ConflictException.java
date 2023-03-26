package ru.practicum.shareit.exceptions;

public class ConflictException extends RuntimeException {
    private final String parameter;

    public ConflictException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}

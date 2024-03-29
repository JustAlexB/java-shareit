package ru.practicum.shareit.exceptions;

public final class ErrorResponse {
    private final String error;
    private final String description;


    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }

}

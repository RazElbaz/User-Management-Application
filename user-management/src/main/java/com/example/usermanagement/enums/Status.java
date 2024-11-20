package com.example.usermanagement.enums;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {
    ACTIVE,
    INACTIVE;

    @JsonCreator
    public static Status fromString(String value) {
        return Status.valueOf(value.toUpperCase());
    }
}
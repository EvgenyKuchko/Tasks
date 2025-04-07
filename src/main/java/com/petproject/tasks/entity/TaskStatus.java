package com.petproject.tasks.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum TaskStatus {
    ACTIVE("Active"),
    CANCELED("Canceled"),
    DONE("Completed");

    private final String displayName;
}
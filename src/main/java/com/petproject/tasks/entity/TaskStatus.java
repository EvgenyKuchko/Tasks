package com.petproject.tasks.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum TaskStatus {
    ACTIVE("Активная"),
    CANCELED("Отменено"),
    DONE("Выполено");

    private final String displayName;
}
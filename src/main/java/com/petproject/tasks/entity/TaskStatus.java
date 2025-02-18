package com.petproject.tasks.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum TaskStatus {
    TODO("Не начато"),
    IN_PROGRESS("В процессе"),
    DONE("Выполено"),
    POSTPONED("Отложено");

    private final String displayName;
}
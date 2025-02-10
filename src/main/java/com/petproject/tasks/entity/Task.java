package com.petproject.tasks.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Task {
    private Long id;
    private String title;
    private String description;
    private LocalDate creationDate;
    private LocalDate dueDate;
    private TaskStatus status;
}
package com.petproject.tasks.dto;

import com.petproject.tasks.entity.TaskStatus;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TaskDto implements Dto{
    private Long id;
    private String title;
    private String description;
    private LocalDate creationDate;
    private LocalDate dueDate;
    private TaskStatus status;
}
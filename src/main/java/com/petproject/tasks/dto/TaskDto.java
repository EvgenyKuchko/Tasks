package com.petproject.tasks.dto;

import com.petproject.tasks.entity.TaskStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TaskDto implements Dto {
    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private TaskStatus status;
}
package com.petproject.tasks.dto;

import com.petproject.tasks.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "title cannot be empty")
    private String title;
    @NotBlank(message = "description cannot be empty")
    private String description;
    @NotNull(message = "date cannot be null")
    private LocalDate date;
    private TaskStatus status;
    private String username;
}
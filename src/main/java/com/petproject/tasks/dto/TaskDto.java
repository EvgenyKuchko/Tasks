package com.petproject.tasks.dto;

import com.petproject.tasks.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TaskDto implements Dto, Comparable<TaskDto> {
    private Long id;
    @NotBlank(message = "title cannot be empty")
    private String title;
    @NotBlank(message = "description cannot be empty")
    private String description;
    @NotNull(message = "date cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    private TaskStatus status;
    @NotEmpty(message = "username cannot be empty")
    private String username;

    @Override
    public int compareTo(TaskDto o) {
        return Long.compare(o.getId(), this.id);
    }
}
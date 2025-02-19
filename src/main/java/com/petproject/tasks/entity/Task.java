package com.petproject.tasks.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity(name = "tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task implements EntityObj{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDate creationDate;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
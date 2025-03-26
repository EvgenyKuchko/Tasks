package com.petproject.tasks.dto;

import com.petproject.tasks.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto implements Dto {
    private Long id;

    @NotBlank(message = "username cannot be empty", groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 5, message = "username must be 5 or more characters", groups = {OnCreate.class, OnUpdate.class})
    private String username;

    @NotBlank(message = "password cannot be empty", groups = OnCreate.class) // Только при регистрации
    @Size(min = 5, message = "password must be 5 or more characters", groups = {OnCreate.class, OnUpdate.class})
    private String password;

    @NotBlank(message = "first name cannot be empty", groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 2, message = "first name must be 2 or more characters", groups = {OnCreate.class, OnUpdate.class})
    private String firstName;

    private Set<UserRole> roles;

    public interface OnCreate {}  // Валидация при создании
    public interface OnUpdate {}  // Валидация при обновлении
}
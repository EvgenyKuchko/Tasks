package com.petproject.tasks.dto;

import com.petproject.tasks.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto implements Dto{
    private Long id;
    @NotBlank(message = "username cannot be empty")
    @Size(min = 5, message = "username must be 5 or more characters")
    private String username;
//    @NotBlank(message = "password cannot be empty")
    @Size(min = 5, message = "password must be 5 or more characters")
    private String password;
    @NotBlank(message = "first name cannot be empty")
    @Size(min = 2, message = "first name must be 2 or more characters")
    private String firstName;
    private Set<UserRole> roles;
}
package com.petproject.tasks.dto;

import com.petproject.tasks.entity.UserRole;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {
    private String username;
    private String password;
    private String firstName;
    private Set<UserRole> roles;
}
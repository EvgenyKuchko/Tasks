package com.petproject.tasks.transformer;

import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserTransformer implements Transformer<User, UserDto> {

    @Override
    public UserDto transform(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();
    }

    @Override
    public User transform(UserDto userDto) {
        return User.builder()
                .firstName(userDto.getFirstName())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .roles(userDto.getRoles())
                .build();
    }
}
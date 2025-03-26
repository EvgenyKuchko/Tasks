package com.petproject.tasks.transformer;

import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.User;
import com.petproject.tasks.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class UserTransformerTest {

    @Autowired
    private UserTransformer userTransformer;

    private final String FIRSTNAME = "Ali";
    private final String USERNAME = "ali34";
    private final String PASSWORD = "12345";

    @Test
    public void testTransformUserToUserDto_Success_TransformAndReturnDto() {
        User user = User.builder()
                .firstName(FIRSTNAME)
                .username(USERNAME)
                .password(PASSWORD)
                .roles(new HashSet<>(Set.of(UserRole.USER, UserRole.ADMIN)))
                .build();

        UserDto resultingUser = userTransformer.transform(user);

        assertThat(resultingUser).isNotNull();
        assertThat(resultingUser.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(resultingUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(resultingUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(resultingUser.getRoles().size()).isEqualTo(2);
    }

    @Test
    public void testTransformUserDtoToUser_Success_TransformAndReturnPOJO() {
        UserDto userDto = UserDto.builder()
                .firstName(FIRSTNAME)
                .username(USERNAME)
                .password(PASSWORD)
                .roles(new HashSet<>(Set.of(UserRole.USER)))
                .build();

        User resultingUser = userTransformer.transform(userDto);

        assertThat(resultingUser).isNotNull();
        assertThat(resultingUser.getFirstName()).isEqualTo(userDto.getFirstName());
        assertThat(resultingUser.getUsername()).isEqualTo(userDto.getUsername());
        assertThat(resultingUser.getPassword()).isEqualTo(userDto.getPassword());
        assertThat(resultingUser.getRoles().size()).isEqualTo(1);
    }
}
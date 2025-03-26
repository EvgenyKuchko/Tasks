package com.petproject.tasks.repository;

import com.petproject.tasks.entity.User;
import com.petproject.tasks.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .firstName("Ann")
                .username("useru")
                .password("passwo")
                .roles(Collections.singleton(UserRole.USER))
                .build();

        userRepository.save(user);
    }

    @Test
    public void testFindByUsername_Success_ReturnUserByUsername() {
        User foundUser = userRepository.findByUsername(user.getUsername());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(foundUser.getFirstName()).isEqualTo(user.getFirstName());
    }

    @Test
    public void testFindAll_Success_ReturnAllUsers() {
        List<User> foundUsers = userRepository.findAll();

        assertThat(foundUsers).isNotNull();
        assertThat(foundUsers.size()).isEqualTo(1);
    }
}
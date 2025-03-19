package com.petproject.tasks.controller;

import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.User;
import com.petproject.tasks.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);
    }

    @Test
    public void shouldShowRegistrationPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    public void shouldSaveNewUserAndRedirectToLoginPage() throws Exception {
        UserDto userDto = UserDto.builder()
                .firstName("Ali")
                .username("ali123")
                .password("passs")
                .build();

        when(userService.existsUsername(userDto.getUsername())).thenReturn(false);
        when(userService.registerUser(userDto)).thenReturn(new User());

        mockMvc.perform(post("/register")
                        .flashAttr("user", userDto)
                        .with(csrf()))  // Добавляем CSRF токен
                .andExpect(status().isOk())  // Ожидаем редирект
                .andExpect(redirectedUrl("/login"));  // Проверка редиректа на страницу входа

        verify(userService, times(1)).registerUser(userDto);  // Проверяем вызов метода регистрации
    }
}
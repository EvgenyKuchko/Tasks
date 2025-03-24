package com.petproject.tasks.controller;

import com.petproject.tasks.config.SecurityConfig;
import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.UserRole;
import com.petproject.tasks.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@Import(SecurityConfig.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    public void shouldReturnLoginPage() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

//    @Test
//    public void shouldSuccessLoginUserAndRedirectToTasksPage() throws Exception {
//        String username = "user123";
//
//        UserDto userDto = UserDto.builder()
//                .id(1L)
//                .firstName("Zia")
//                .username(username)
//                .password("pass123")
//                .roles(Collections.singleton(UserRole.USER))
//                .build();
//
//        when(userService.getUserByUsername(username)).thenReturn(userDto);
//
//        this.mockMvc.perform(post("/login").with(csrf())
//                .param("username", userDto.getUsername())
//                .param("password", userDto.getPassword()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/tasks/" + userDto.getId()));
//    }
}
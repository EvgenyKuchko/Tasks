package com.petproject.tasks.controller;

import com.petproject.tasks.config.SecurityConfig;
import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.UserRole;
import com.petproject.tasks.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private UserDto userDto;
    private List<SimpleGrantedAuthority> adminRole;

    @BeforeEach
    public void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .firstName("Mike")
                .username("admin123")
                .password("adm123")
                .roles(Set.of(UserRole.USER, UserRole.ADMIN))
                .build();
    }

    @Test
    public void shouldShowAdminPage() throws Exception {
        this.mockMvc.perform(get("/admin").with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.ADMIN.name()))))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));
    }

    @Test
    public void shouldBanAccessToPageWithUserRole() throws Exception {
        this.mockMvc.perform(get("/admin").with(csrf())
                        .with(user(userDto.getUsername()).authorities(new SimpleGrantedAuthority(UserRole.USER.name()))))
                .andExpect(status().is4xxClientError());
    }
}
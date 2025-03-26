package com.petproject.tasks.config;

import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginSuccessHandlerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LoginSuccessHandler loginSuccessHandler;

    private String username;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        username = "spy12";
    }

    @Test
    void testOnAuthenticationSuccess_userFound_redirectsToUserPage() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);

        when(authentication.getName()).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(userDto);

        loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("/tasks/1");
    }

    @Test
    void testOnAuthenticationSuccess_userNotFound_redirectsToLoginWithError() throws Exception {
        when(authentication.getName()).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(null);

        loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("/login?error");
    }
}
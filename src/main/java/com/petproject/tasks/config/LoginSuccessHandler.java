package com.petproject.tasks.config;

import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        String username = authentication.getName();

        UserDto userDto = userService.getUserByUsername(username);
        if (userDto != null) {
            Long userId = userDto.getId();
            response.sendRedirect("/tasks/" + userId);
        } else {
            response.sendRedirect("/login?error");
        }
    }
}
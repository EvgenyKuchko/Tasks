package com.petproject.tasks.config;

import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.User;
import com.petproject.tasks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        // Получаем username (email, login)
        String username = authentication.getName();

        // Находим пользователя в базе
        UserDto userDto = userService.getUserByUsername(username);
        if (userDto != null) {
            Long userId = userDto.getId(); // Достаем ID из базы
            response.sendRedirect("/tasks/" + userId); // Редиректим на нужную страницу
        } else {
            response.sendRedirect("/login?error"); // Если что-то не так
        }
    }
}
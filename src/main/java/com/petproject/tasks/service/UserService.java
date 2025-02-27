package com.petproject.tasks.service;

import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.User;
import com.petproject.tasks.entity.UserRole;
import com.petproject.tasks.repository.UserRepository;
import com.petproject.tasks.transformer.UserTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserTransformer userTransformer;

    @Transactional
    public User registerUser(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()) != null) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setUsername(userDto.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        user.setRoles(Collections.singleton(UserRole.USER));
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = userTransformer.transform(userRepository.findByUsername(username));
        if (userDto == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (UserRole role : userDto.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }
        return new org.springframework.security.core.userdetails.User(userDto.getUsername(), userDto.getPassword(), grantedAuthorities);
    }

    @Transactional
    public UserDto getUserById(Long id) {
        return userTransformer.transform(userRepository.getReferenceById(id));
    }

    @Transactional
    public UserDto getUserByUsername(String username) {
        return userTransformer.transform(userRepository.findByUsername(username));
    }

    @Transactional
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(x -> userTransformer.transform(x))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addOrRemoveAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Set<UserRole> newRoles = new HashSet<>(user.getRoles());
        if (newRoles.contains(UserRole.ADMIN)) {
            newRoles.remove(UserRole.ADMIN);
        } else {
            newRoles.add(UserRole.ADMIN);
        }
        user.setRoles(newRoles);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUserByUserId(Long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public void updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(userDto.getFirstName() != null && !userDto.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(userDto.getFirstName());
        }
        if(userDto.getUsername() != null && !userDto.getUsername().equals(user.getUsername())) {
            user.setUsername(userDto.getUsername());
        }
        if(userDto.getPassword() != null && !userDto.getPassword().equals(user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        }
        userRepository.save(user);
    }
}
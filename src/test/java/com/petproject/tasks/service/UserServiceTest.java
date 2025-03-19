package com.petproject.tasks.service;

import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.User;
import com.petproject.tasks.entity.UserRole;
import com.petproject.tasks.repository.UserRepository;
import com.petproject.tasks.transformer.UserTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserTransformer userTransformer;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserDto userDto;
    private User user;
    private final String USERNAME = "aliali";
    private final Long USER_ID = 99L;
    private final String ENCODED_PASS = "encodedPassword";

    @BeforeEach
    public void setUp() {
        userDto = UserDto.builder()
                .firstName("Ali")
                .username(USERNAME)
                .password("password")
                .roles(Collections.singleton(UserRole.USER))
                .build();

        user = User.builder()
                .firstName("Ali")
                .username(USERNAME)
                .password("password")
                .roles(Collections.singleton(UserRole.USER))
                .build();
    }

    @Test
    public void shouldSaveUserOnRegistration() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);
        when(userTransformer.transform(userDto)).thenReturn(user);
        when(bCryptPasswordEncoder.encode(userDto.getPassword())).thenReturn(ENCODED_PASS);

        userService.registerUser(userDto);

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userTransformer, times(1)).transform(userDto);
        verify(bCryptPasswordEncoder, times(1)).encode(userDto.getPassword());
        verify(userRepository, times(1)).save(user);


        assertThat(user.getFirstName()).isEqualTo(userDto.getFirstName());
        assertThat(user.getUsername()).isEqualTo(userDto.getUsername());
        assertThat(user.getPassword()).isEqualTo(ENCODED_PASS);
        assertThat(user.getRoles()).isNotNull();
        assertThat(user.getRoles().size()).isEqualTo(1);
        assertThat(user.getRoles().contains(UserRole.USER)).isEqualTo(true);
    }

    @Test
    public void shouldFailSaveUserOnRegistrationWithUsername() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);

        Throwable usernameAlreadyTakenEx = assertThrows(RuntimeException.class, () -> userService.registerUser(userDto));

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userTransformer, times(0)).transform(userDto);
        verify(bCryptPasswordEncoder, times(0)).encode(userDto.getPassword());
        verify(userRepository, times(0)).save(user);

        assertThat("Username already taken").isEqualTo(usernameAlreadyTakenEx.getMessage());
    }

    @Test
    public void shouldFailLoadUserByUsername() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);

        Throwable usernameNotFoundEx = assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(USERNAME));

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userTransformer, times(0)).transform(userDto);

        assertThat("User not found").isEqualTo(usernameNotFoundEx.getMessage());
    }

    @Test
    public void shouldLoadUserByUsernameAndReturnUser() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(userTransformer.transform(user)).thenReturn(userDto);

        UserDetails userDetails = userService.loadUserByUsername(USERNAME);

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userTransformer, times(1)).transform(user);

        assertThat(userDto.getUsername()).isEqualTo(userDetails.getUsername());
        assertThat(userDto.getPassword()).isEqualTo(userDetails.getPassword());
    }

    @Test
    public void shouldGetAndReturnUserById() {
        when(userRepository.getReferenceById(USER_ID)).thenReturn(user);
        when(userTransformer.transform(user)).thenReturn(userDto);

        UserDto resultingUserDto = userService.getUserById(USER_ID);

        verify(userRepository, times(1)).getReferenceById(USER_ID);
        verify(userTransformer, times(1)).transform(user);

        assertThat(resultingUserDto.getFirstName()).isEqualTo(userDto.getFirstName());
        assertThat(resultingUserDto.getUsername()).isEqualTo(userDto.getUsername());
    }

    @Test
    public void shouldFindAndReturnUserByUsername() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(userTransformer.transform(user)).thenReturn(userDto);

        UserDto resultingUserDto = userService.getUserByUsername(USERNAME);

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userTransformer, times(1)).transform(user);

        assertThat(resultingUserDto.getFirstName()).isEqualTo(userDto.getFirstName());
        assertThat(resultingUserDto.getUsername()).isEqualTo(userDto.getUsername());
    }

    @Test
    public void shouldReturnAllUsers() {
        List<User> DBUsers = new ArrayList<>();
        User userOne = User.builder()
                .firstName("Say")
                .username("capo9")
                .password("pass333")
                .roles(Collections.singleton(UserRole.USER))
                .build();
        DBUsers.add(user);
        DBUsers.add(userOne);

        when(userRepository.findAll()).thenReturn(DBUsers);

        List<UserDto> resultingDtoUsers = userService.getAllUsers();

        verify(userRepository, times(1)).findAll();
        verify(userTransformer, times(1)).transform(user);
        verify(userTransformer, times(1)).transform(userOne);

        assertThat(resultingDtoUsers.size()).isEqualTo(2);
    }

    @Test
    public void shouldFailAddAdminRoleAndThrowUserNotFoundEx() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        Throwable userNotFoundEx = assertThrows(RuntimeException.class, () -> userService.addOrRemoveAdminRole(USER_ID));

        verify(userRepository, times(1)).findById(USER_ID);
        verifyNoMoreInteractions(userRepository);

        assertThat("User not found").isEqualTo(userNotFoundEx.getMessage());
    }

    @Test
    public void ShouldSuccessAddAdminRoleToUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.ofNullable(user));

        userService.addOrRemoveAdminRole(USER_ID);

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).save(user);

        assertThat(user.getRoles().size()).isEqualTo(2);
        assertThat(user.getRoles().contains(UserRole.ADMIN)).isEqualTo(true);
    }

    @Test
    public void ShouldSuccessRemoveAdminRoleToUser() {
        Set<UserRole> roles = new HashSet<>(user.getRoles());
        roles.add(UserRole.ADMIN);
        user.setRoles(roles);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.ofNullable(user));

        userService.addOrRemoveAdminRole(USER_ID);

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).save(user);

        assertThat(user.getRoles().size()).isEqualTo(1);
        assertThat(user.getRoles().contains(UserRole.USER)).isEqualTo(true);
    }

    @Test
    public void shouldSuccessRemoveUserById() {
        userService.deleteUserByUserId(USER_ID);

        verify(userRepository, times(1)).deleteById(USER_ID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void shouldFailUpdateAndThrowUserNotFoundEx() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        Throwable userNotFoundEx = assertThrows(RuntimeException.class, () -> userService.updateUser(USER_ID, userDto));

        verify(userRepository, times(1)).findById(USER_ID);
        verifyNoMoreInteractions(userRepository);

        assertThat("User not found").isEqualTo(userNotFoundEx.getMessage());
    }

    @Test
    public void shouldSuccessUpdateUsernameAndPasswordAndSaveUser() {
        UserDto updatedUserDto = UserDto.builder()
                .username("skydiver")
                .password("sky96diver")
                .build();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.ofNullable(user));
        when(bCryptPasswordEncoder.encode(updatedUserDto.getPassword())).thenReturn(ENCODED_PASS);

        userService.updateUser(USER_ID, updatedUserDto);

        verify(userRepository, times(1)).findById(USER_ID);
        verify(bCryptPasswordEncoder, times(1)).encode(updatedUserDto.getPassword());
        verify(userRepository, times(1)).save(user);

        assertThat(user.getPassword()).isEqualTo(ENCODED_PASS);
        assertThat(user.getUsername()).isEqualTo(updatedUserDto.getUsername());
    }
}
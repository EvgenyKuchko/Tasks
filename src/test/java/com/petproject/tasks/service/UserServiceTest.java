package com.petproject.tasks.service;

import com.petproject.tasks.dto.TaskDto;
import com.petproject.tasks.dto.UserDto;
import com.petproject.tasks.entity.Task;
import com.petproject.tasks.entity.TaskStatus;
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
    public void testRegisterUser_Success_TransformToPOJOAndSaved() {
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
    public void testRegisterUser_FailWithUsernameAlreadyTakenException_DidNotSave() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);

        Throwable usernameAlreadyTakenEx = assertThrows(RuntimeException.class, () -> userService.registerUser(userDto));

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userTransformer, times(0)).transform(userDto);
        verify(bCryptPasswordEncoder, times(0)).encode(userDto.getPassword());
        verify(userRepository, times(0)).save(user);

        assertThat("Username already taken").isEqualTo(usernameAlreadyTakenEx.getMessage());
    }

    @Test
    public void testLoadUserByUsername_FailWithUserNotFoundException_DidNotReturnUser() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);

        Throwable usernameNotFoundEx = assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(USERNAME));

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userTransformer, times(0)).transform(userDto);

        assertThat("User not found").isEqualTo(usernameNotFoundEx.getMessage());
    }

    @Test
    public void testLoadUserByUsername_Success_TransformToDtoAndReturn() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(userTransformer.transform(user)).thenReturn(userDto);

        UserDetails userDetails = userService.loadUserByUsername(USERNAME);

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userTransformer, times(1)).transform(user);

        assertThat(userDto.getUsername()).isEqualTo(userDetails.getUsername());
        assertThat(userDto.getPassword()).isEqualTo(userDetails.getPassword());
    }

    @Test
    public void testGetUserById_Success_TransformToDtoAndReturn() {
        when(userRepository.getReferenceById(USER_ID)).thenReturn(user);
        when(userTransformer.transform(user)).thenReturn(userDto);

        UserDto resultingUserDto = userService.getUserById(USER_ID);

        verify(userRepository, times(1)).getReferenceById(USER_ID);
        verify(userTransformer, times(1)).transform(user);

        assertThat(resultingUserDto.getFirstName()).isEqualTo(userDto.getFirstName());
        assertThat(resultingUserDto.getUsername()).isEqualTo(userDto.getUsername());
    }

    @Test
    public void testGetUserByUsername_Success_TransformAndReturn() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(userTransformer.transform(user)).thenReturn(userDto);

        UserDto resultingUserDto = userService.getUserByUsername(USERNAME);

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(userTransformer, times(1)).transform(user);

        assertThat(resultingUserDto.getFirstName()).isEqualTo(userDto.getFirstName());
        assertThat(resultingUserDto.getUsername()).isEqualTo(userDto.getUsername());
    }

    @Test
    public void testGetAllUsers_Success_TransformToDtoAndReturnListOfUsers() {
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
    public void testAddOrRemoveAdminRole_FailWithUserNotFoundException_DidNotAddOrRemoveAdminRole() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        Throwable userNotFoundEx = assertThrows(RuntimeException.class, () -> userService.addOrRemoveAdminRole(USER_ID));

        verify(userRepository, times(1)).findById(USER_ID);
        verifyNoMoreInteractions(userRepository);

        assertThat("User not found").isEqualTo(userNotFoundEx.getMessage());
    }

    @Test
    public void testAddOrRemoveAdminRole_Success_AddAdminRole() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.ofNullable(user));

        userService.addOrRemoveAdminRole(USER_ID);

        verify(userRepository, times(1)).findById(USER_ID);
        verify(userRepository, times(1)).save(user);

        assertThat(user.getRoles().size()).isEqualTo(2);
        assertThat(user.getRoles().contains(UserRole.ADMIN)).isEqualTo(true);
    }

    @Test
    public void testAddOrRemoveAdminRole_Success_RemoveAdminRole() {
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
    public void testDeleteUserByUserId_Success_DeletedUserById() {
        userService.deleteUserByUserId(USER_ID);

        verify(userRepository, times(1)).deleteById(USER_ID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testUpdateUser_FailWithUserNotFoundException_DidNotUpdate() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        Throwable userNotFoundEx = assertThrows(RuntimeException.class, () -> userService.updateUser(USER_ID, userDto));

        verify(userRepository, times(1)).findById(USER_ID);
        verifyNoMoreInteractions(userRepository);

        assertThat("User not found").isEqualTo(userNotFoundEx.getMessage());
    }

    @Test
    public void testUpdateUser_Success_UserUpdated() {
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

    @Test
    public void testGetFilteredUsers_Success_FindTransformedToDtoAndReturnList() {
        Set<Task> tasks = new HashSet<>();

        User user1 = new User(1L, "oscar99", "qqqqq", "oscar", new HashSet<>(Collections.singleton(UserRole.USER)), tasks);
        User user2 = new User(2L, "stan00", "stan71", "stan", new HashSet<>(Collections.singleton(UserRole.USER)), tasks);
        User user3 = new User(3L, "chany43", "chanyy", "chan", new HashSet<>(Collections.singleton(UserRole.USER)), tasks);
        User user4 = new User(4L, "koly2", "asdwq", "koly", new HashSet<>(Collections.singleton(UserRole.USER)), tasks);

        List<User> mockUsers = Arrays.asList(user1, user2, user3, user4);

        UserDto userDto1 = new UserDto(1L, "oscar99", "qqqqq", "oscar", new HashSet<>(Collections.singleton(UserRole.USER)));
        UserDto userDto2 = new UserDto(2L, "stan00", "stan71", "stan", new HashSet<>(Collections.singleton(UserRole.USER)));
        UserDto userDto3 = new UserDto(3L, "chany43", "chanyy", "chan", new HashSet<>(Collections.singleton(UserRole.ADMIN)));
        UserDto userDto4 = new UserDto(4L, "koly2", "asdwq", "koly", new HashSet<>(Collections.singleton(UserRole.USER)));

        when(userRepository.findAll()).thenReturn(mockUsers);
        when(userTransformer.transform(user1)).thenReturn(userDto1);
        when(userTransformer.transform(user2)).thenReturn(userDto2);
        when(userTransformer.transform(user3)).thenReturn(userDto3);
        when(userTransformer.transform(user4)).thenReturn(userDto4);

        List<UserDto> resultingUsers = userService.getFilteredUsers("stan00", "USER");

        assertThat(resultingUsers.size()).isEqualTo(1);
        assertThat(resultingUsers.contains(userDto2)).isEqualTo(true);

        verify(userRepository, times(1)).findAll();
        verify(userTransformer, times(4)).transform(any(User.class));
    }

    @Test
    public void testExistUsername_Success_ReturnTrue() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

        boolean result = userService.existsUsername(USERNAME);

        assertThat(result).isEqualTo(true);

        verify(userRepository, times(1)).existsByUsername(any());
    }
}
package com.petproject.tasks.repository;

import com.petproject.tasks.entity.Task;
import com.petproject.tasks.entity.TaskStatus;
import com.petproject.tasks.entity.User;
import com.petproject.tasks.entity.UserRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private User user;
    private Task task;
    private final String TITLE = "cooking";
    private final LocalDate DATE = LocalDate.parse("2025-05-07");

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .firstName("Ann")
                .username("useru")
                .password("passwo")
                .roles(Collections.singleton(UserRole.USER))
                .build();

        userRepository.save(user);

        task = Task.builder()
                .title(TITLE)
                .description("cook the cake")
                .date(DATE)
                .status(TaskStatus.ACTIVE)
                .user(user)
                .build();

        taskRepository.save(task);

        Task taskTwo = Task.builder()
                .title("reading")
                .description("read the book")
                .date(LocalDate.parse("2025-04-10"))
                .status(TaskStatus.ACTIVE)
                .user(user)
                .build();

        taskRepository.save(taskTwo);
    }

    @Test
    public void shouldReturnTaskByUserId() {
        List<Task> foundTask = taskRepository.getTasksByUserId(user.getId());

        assertThat(foundTask).isNotNull();
        assertThat(foundTask.get(0).getTitle()).isEqualTo(TITLE);
        assertThat(foundTask.get(0).getStatus()).isEqualTo(TaskStatus.ACTIVE);
        assertThat(foundTask.get(0).getDate()).isEqualTo(DATE);
    }

    @Test
    public void shouldReturnTaskByUserIdAndDate() {
        List<Task> foundTask = taskRepository.getTasksByUserIdAndDate(user.getId(), DATE);

        assertThat(foundTask).isNotNull();
        assertThat(foundTask.get(0).getTitle()).isEqualTo(TITLE);
        assertThat(foundTask.get(0).getStatus()).isEqualTo(TaskStatus.ACTIVE);
        assertThat(foundTask.get(0).getDate()).isEqualTo(DATE);
    }

    @Test
    public void shouldReturnTaskById() {
        Optional<Task> foundTask = taskRepository.findById(task.getId());

        assertThat(foundTask).isNotNull();
        assertThat(foundTask.get().getTitle()).isEqualTo(TITLE);
        assertThat(foundTask.get().getStatus()).isEqualTo(TaskStatus.ACTIVE);
        assertThat(foundTask.get().getDate()).isEqualTo(DATE);
    }

    @Test
    public void shouldChangeTaskStatus() {
        taskRepository.changeTaskStatus(task.getId(), TaskStatus.DONE);
        taskRepository.flush();
        entityManager.clear();

        Optional<Task> changedTask = taskRepository.findById(task.getId());

        assertThat(changedTask.get().getTitle()).isEqualTo(TITLE);
        assertThat(changedTask.get().getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    public void shouldReturnTasksByKeyword() {
        List<Task> foundTasks = taskRepository.searchTasks(user.getId(), "cake");

        assertThat(foundTasks).isNotNull();
        assertThat(foundTasks.size()).isEqualTo(1);
        assertThat(foundTasks.get(0).getTitle()).isEqualTo(TITLE);
        assertThat(foundTasks.get(0).getStatus()).isEqualTo(TaskStatus.ACTIVE);
    }

    @Test
    public void shouldReturnAllTasks() {
        List<Task> foundTasks = taskRepository.findAll();

        assertThat(foundTasks).isNotNull();
        assertThat(foundTasks.size()).isEqualTo(2);
    }
}
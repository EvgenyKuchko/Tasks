package com.petproject.tasks.repository;

import com.petproject.tasks.entity.Task;
import com.petproject.tasks.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> getTasksByUserId(Long userId);

    List<Task> getTasksByUserIdAndDate(Long userId, LocalDate localDate);

    @Modifying
    @Query("update tasks t set t.status = :status where t.id = :id")
    void changeTaskStatus(@Param("id") Long id, @Param("status") TaskStatus status);

    @Query("SELECT t FROM tasks t WHERE t.user.id = :userId AND (t.title LIKE CONCAT('%', :keyword, '%') OR t.description LIKE CONCAT('%', :keyword, '%'))")
    List<Task> searchTasks(@Param("userId") Long userId, @Param("keyword") String keyword);
}
package com.petproject.tasks.repository;

import com.petproject.tasks.entity.Task;
import com.petproject.tasks.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> getTasksByUserId(Long userId);
    @Modifying
    @Query("update tasks t set t.status = :status where t.id = :id")
    void changeTaskStatusToDone(@Param("id") Long id, @Param("status") TaskStatus status);
}
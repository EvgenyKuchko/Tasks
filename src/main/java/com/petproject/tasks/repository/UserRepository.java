package com.petproject.tasks.repository;

import com.petproject.tasks.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    List<User> findAll();

    boolean existsByUsername(String username);
}
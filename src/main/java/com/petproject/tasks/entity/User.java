package com.petproject.tasks.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String login;
    private String password;
    private String name;
}
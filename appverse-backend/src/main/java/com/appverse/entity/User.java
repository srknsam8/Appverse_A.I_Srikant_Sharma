package com.appverse.entity;

import com.appverse.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users") // Good practice: plural names for database tables [cite: 276-277]
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
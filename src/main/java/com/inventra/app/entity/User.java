package com.inventra.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Setter
@Getter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String username;
    private String password;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
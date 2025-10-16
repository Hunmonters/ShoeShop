package com.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "Accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @Column(length = 50)
    private String username;

    @Column(length = 100, nullable = false)
    private String fullname;

    @Column(name = "password_hash", length = 200, nullable = false)
    private String passwordHash;

    @Column(length = 120, nullable = false, unique = true)
    private String email;

    @Column(length = 255)
    private String photo;

    @Column(nullable = false)
    private Boolean activated = true;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;

    @Column(length = 200)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private LocalDateTime createDate = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Account_Roles",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private Set<Role> roles;
}
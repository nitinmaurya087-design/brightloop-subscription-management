package com.brightloop.user;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="app_users")
public class AppUser {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=190) private String email;
    @Column(name="password_hash", nullable=false, length=100) private String passwordHash;
    @Column(name="full_name", nullable=false, length=120) private String fullName;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private Role role;
    @Column(nullable=false) private boolean enabled=true;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt=Instant.now();
    @Column(name="updated_at", nullable=false) private Instant updatedAt=Instant.now();
    protected AppUser() {}
    public AppUser(String email,String passwordHash,String fullName,Role role){this.email=email.toLowerCase().trim();this.passwordHash=passwordHash;this.fullName=fullName;this.role=role;}
    @PreUpdate void touch(){updatedAt=Instant.now();}
    public void changePassword(String encoded){passwordHash=encoded;updatedAt=Instant.now();}
    public Long getId(){return id;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;}
    public String getFullName(){return fullName;} public Role getRole(){return role;} public boolean isEnabled(){return enabled;}
}

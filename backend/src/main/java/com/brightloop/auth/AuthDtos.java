package com.brightloop.auth;

import com.brightloop.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public final class AuthDtos {
    private AuthDtos() {}
    public record LoginRequest(@Email @NotBlank String email,@NotBlank String password) {}
    public record RegisterRequest(@Email @NotBlank String email,@NotBlank String fullName,@NotBlank @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{9,}$",message="Password must be 9+ characters and include uppercase, lowercase, and a special character") String password) {}
    public record StaffRequest(@Email @NotBlank String email,@NotBlank String fullName,@NotBlank @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{9,}$",message="Password must be 9+ characters and include uppercase, lowercase, and a special character") String password) {}
    public record UserResponse(Long id,String email,String fullName,Role role) {}
    public record AuthResponse(String accessToken,String tokenType,UserResponse user) {}
}

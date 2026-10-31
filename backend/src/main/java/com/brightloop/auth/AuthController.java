package com.brightloop.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    private final PasswordResetService reset;
    public AuthController(AuthService auth,PasswordResetService reset){this.auth=auth;this.reset=reset;}
    @PostMapping("/login") public AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest request){return auth.login(request);}
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) public AuthDtos.AuthResponse register(@Valid @RequestBody AuthDtos.RegisterRequest request){return auth.register(request);}
    @PostMapping("/staff") @ResponseStatus(HttpStatus.CREATED) @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") public AuthDtos.UserResponse createStaff(@Valid @RequestBody AuthDtos.StaffRequest request){return auth.createStaff(request);}
    public record ForgotRequest(@Email @NotBlank String email){}
    public record ResetRequest(@NotBlank String token,@NotBlank @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{9,}$",message="Password must be 9+ characters and include uppercase, lowercase, and a special character") String newPassword){}
    @PostMapping("/password/forgot") public PasswordResetService.ForgotResponse forgot(@Valid @RequestBody ForgotRequest request){return reset.request(request.email());}
    @PostMapping("/password/reset") @ResponseStatus(HttpStatus.NO_CONTENT) public void reset(@Valid @RequestBody ResetRequest request){reset.reset(request.token(),request.newPassword());}
    @GetMapping("/me") public AuthDtos.UserResponse me(Authentication authentication){return auth.current(authentication.getName());}
}

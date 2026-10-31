package com.brightloop.user;
import com.brightloop.auth.*;import jakarta.validation.Valid;import org.springframework.http.HttpStatus;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/staff") @PreAuthorize("hasRole('ADMIN')") public class StaffController{
 private final AppUserRepository users;private final AuthService auth;public StaffController(AppUserRepository u,AuthService a){users=u;auth=a;}
 public record StaffResponse(Long id,String email,String fullName,String role,boolean enabled){}
 @GetMapping public java.util.List<StaffResponse> list(){return users.findByRole(Role.STAFF).stream().map(u->new StaffResponse(u.getId(),u.getEmail(),u.getFullName(),u.getRole().name(),u.isEnabled())).toList();}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public AuthDtos.UserResponse create(@Valid @RequestBody AuthDtos.StaffRequest request){return auth.createStaff(request);}
}

package com.brightloop.auth;

import com.brightloop.security.JwtService;
import com.brightloop.catalog.Customer;
import com.brightloop.catalog.CustomerRepository;
import com.brightloop.notification.EmailNotificationService;
import com.brightloop.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final AppUserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;private final CustomerRepository customers;private final EmailNotificationService emails;
    public AuthService(AppUserRepository users,PasswordEncoder encoder,JwtService jwt,CustomerRepository customers,EmailNotificationService emails){this.users=users;this.encoder=encoder;this.jwt=jwt;this.customers=customers;this.emails=emails;}
    @Transactional(readOnly=true) public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request){
        AppUser user=users.findByEmailIgnoreCase(request.email().trim()).filter(AppUser::isEnabled).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));
        if(!encoder.matches(request.password(),user.getPasswordHash()))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password");
        return response(user);
    }
    @Transactional public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request){
        String email=request.email().trim().toLowerCase();if(users.existsByEmailIgnoreCase(email))throw new ResponseStatusException(HttpStatus.CONFLICT,"Email is already registered");
        AppUser user=users.save(new AppUser(email,encoder.encode(request.password()),request.fullName().trim(),Role.CUSTOMER));if(!customers.existsByEmailIgnoreCase(email))customers.save(new Customer(request.fullName().trim(),email,null,null,null,null,null,null,null,null));emails.welcome(email,request.fullName().trim());return response(user);
    }
    @Transactional public AuthDtos.UserResponse createStaff(AuthDtos.StaffRequest request){String email=request.email().trim().toLowerCase();if(users.existsByEmailIgnoreCase(email))throw new ResponseStatusException(HttpStatus.CONFLICT,"Email is already registered");AppUser user=users.save(new AppUser(email,encoder.encode(request.password()),request.fullName().trim(),Role.STAFF));return userResponse(user);}
    @Transactional(readOnly=true) public AuthDtos.UserResponse current(String email){return users.findByEmailIgnoreCase(email).map(this::userResponse).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Account not found"));}
    private AuthDtos.AuthResponse response(AppUser user){return new AuthDtos.AuthResponse(jwt.issue(user),"Bearer",userResponse(user));}
    private AuthDtos.UserResponse userResponse(AppUser u){return new AuthDtos.UserResponse(u.getId(),u.getEmail(),u.getFullName(),u.getRole());}
}

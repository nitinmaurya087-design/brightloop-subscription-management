package com.brightloop.security;

import com.brightloop.user.AppUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt; private final AppUserRepository users;
    public JwtAuthenticationFilter(JwtService jwt,AppUserRepository users){this.jwt=jwt;this.users=users;}
    @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
        String auth=req.getHeader("Authorization");
        if(auth!=null&&auth.startsWith("Bearer ")&&SecurityContextHolder.getContext().getAuthentication()==null){
            try {String email=jwt.subject(auth.substring(7));users.findByEmailIgnoreCase(email).filter(u->u.isEnabled()).ifPresent(u->{var authorities=java.util.List.of(new SimpleGrantedAuthority("ROLE_"+u.getRole().name()));var token=new UsernamePasswordAuthenticationToken(u.getEmail(),null,authorities);SecurityContextHolder.getContext().setAuthentication(token);});}
            catch(RuntimeException ignored){SecurityContextHolder.clearContext();}
        }
        chain.doFilter(req,res);
    }
}

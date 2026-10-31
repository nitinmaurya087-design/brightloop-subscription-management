package com.brightloop.security;

import com.brightloop.user.AppUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey key; private final long expirationMs;
    public JwtService(@Value("${brightloop.jwt.secret}") String secret,@Value("${brightloop.jwt.expiration-ms}") long expirationMs){
        byte[] bytes=secret.getBytes(StandardCharsets.UTF_8);
        if(bytes.length<32) throw new IllegalArgumentException("JWT_SECRET must be at least 32 bytes");
        this.key=Keys.hmacShaKeyFor(bytes); this.expirationMs=expirationMs;
    }
    public String issue(AppUser user){Date now=new Date();return Jwts.builder().subject(user.getEmail()).claim("role",user.getRole().name()).issuedAt(now).expiration(new Date(now.getTime()+expirationMs)).signWith(key).compact();}
    public String subject(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();}
}

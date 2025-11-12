package com.inventra.app.config;


import com.inventra.app.entity.User;
import com.inventra.app.repository.UserRepository;
import com.inventra.app.util.LoadDataProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {

    private final UserRepository userRepository;
    private final LoadDataProperties loadDataProperties;

    public JwtService(UserRepository userRepository, LoadDataProperties loadDataProperties) {
        this.userRepository = userRepository;
        this.loadDataProperties = loadDataProperties;
    }
    public String generateToken(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(loadDataProperties.getExpirationMinutes(), ChronoUnit.MINUTES)))
                .signWith(Keys.hmacShaKeyFor(loadDataProperties.getSecret().getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }


    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(loadDataProperties.getSecret().getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()));
    }
}
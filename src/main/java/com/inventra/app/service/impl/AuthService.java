package com.inventra.app.service.impl;

import com.inventra.app.config.JwtService;
import com.inventra.app.config.exceptions.UnauthorizedException;
import com.inventra.app.entity.dto.AuthResponse;
import com.inventra.app.entity.dto.LoginRequest;
import com.inventra.app.entity.dto.UserDTO;
import com.inventra.app.service.IUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IUserService userService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, IUserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public AuthResponse login(LoginRequest request) {
        logger.info("Login request received");
        var userDto = userService.getByUsername(request.getUsername().trim());

        if(userDto.isEmpty()){
            logger.error("User not found");
            throw new UnauthorizedException("User or password invalid");
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));


            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtService.generateToken(user);

            logger.info("Login successful");
            return new AuthResponse(token, user.getUsername(), user.getAuthorities().toString());
        } catch (BadCredentialsException e) {
            logger.error("User or password invalid");
            throw new UnauthorizedException("User or password invalid.");
        }
    }
}

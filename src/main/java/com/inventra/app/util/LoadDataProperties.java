package com.inventra.app.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDataProperties {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.minutes}")
    private Long expirationMinutes;

    public Long getExpirationMinutes() {
        return expirationMinutes;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
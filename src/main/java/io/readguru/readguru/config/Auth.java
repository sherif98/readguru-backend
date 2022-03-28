package io.readguru.readguru.config;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

public class Auth {

    public static String currentUserId(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getSubject();
    }
}

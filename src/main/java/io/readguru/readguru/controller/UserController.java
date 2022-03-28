package io.readguru.readguru.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.readguru.readguru.config.Auth;
import io.readguru.readguru.domain.User;
import io.readguru.readguru.repository.UserRepository;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user")
    public User addUser(@AuthenticationPrincipal Jwt jwt) {
        Optional<User> user = userRepository.findById(Auth.currentUserId(jwt));
        return user.orElseGet(() -> userRepository.save(User.builder()
                .id(Auth.currentUserId(jwt))
                .email(jwt.getClaimAsString("email"))
                .name(jwt.getClaimAsString("email"))
                .build()));
    }
}

package io.readguru.readguru.controller;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.readguru.readguru.config.Auth;
import io.readguru.readguru.domain.Title;
import io.readguru.readguru.domain.User;
import io.readguru.readguru.dto.AddTitleRequest;
import io.readguru.readguru.repository.TitleRepository;

@RestController
public class TitleController {

    @Autowired
    private TitleRepository titleRepository;

    @PostMapping("/title")
    public Title addTitle(@RequestBody AddTitleRequest addTitleRequest, @AuthenticationPrincipal Jwt jwt) {
        return titleRepository.save(Title.builder()
                .titleName(addTitleRequest.getTitleName())
                .author(addTitleRequest.getAuthor())
                .cover(addTitleRequest.getCover())
                .user(User.builder().id(Auth.currentUserId(jwt)).build())
                .build());
    }

    @GetMapping("/title/{titleId}")
    public Title getTitle(@PathVariable int titleId, @AuthenticationPrincipal Jwt jwt) {
        return titleRepository.findByIdAndUserId(titleId, Auth.currentUserId(jwt))
                .orElseThrow(() -> new IllegalArgumentException());
    }

    @GetMapping("/title")
    public Set<Title> listTitles(@AuthenticationPrincipal Jwt jwt) {
        return titleRepository.findByUserId(Auth.currentUserId(jwt));
    }

    @DeleteMapping("/title/{titleId}")
    @Transactional
    public void removeTitle(@PathVariable int titleId, @AuthenticationPrincipal Jwt jwt) {
        titleRepository.deleteByIdAndUserId(titleId, Auth.currentUserId(jwt));
    }
}

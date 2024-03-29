package io.readguru.readguru.controller;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.readguru.readguru.config.Auth;
import io.readguru.readguru.domain.Tag;
import io.readguru.readguru.repository.TagRepository;

@RestController
public class TagController {

    @Autowired
    private TagRepository tagRepository;

    @GetMapping("/tag")
    public Set<Tag> listTags(@AuthenticationPrincipal Jwt jwt) {
        return tagRepository.findByUserId(Auth.currentUserId(jwt));
    }

    @DeleteMapping("/tag/{tagId}")
    @Transactional
    public ResponseEntity<Void> removeTag(@PathVariable String tagId, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("sherifeid:" + tagId);
        if (!tagRepository.findByIdAndUserId(tagId, Auth.currentUserId(jwt)).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        tagRepository.deleteByIdAndUserId(tagId, Auth.currentUserId(jwt));
        return ResponseEntity.ok().build();
    }

}

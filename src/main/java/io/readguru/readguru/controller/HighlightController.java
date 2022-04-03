package io.readguru.readguru.controller;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.readguru.readguru.config.Auth;
import io.readguru.readguru.domain.Highlight;
import io.readguru.readguru.domain.Tag;
import io.readguru.readguru.domain.Title;
import io.readguru.readguru.domain.User;
import io.readguru.readguru.dto.AddHighlightRequest;
import io.readguru.readguru.dto.TagHighlightRequest;
import io.readguru.readguru.repository.HighlightRepository;
import io.readguru.readguru.repository.TagRepository;
import io.readguru.readguru.repository.TitleRepository;

@RestController
public class HighlightController {

    @Autowired
    private HighlightRepository highlightRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TitleRepository titleRepository;

    @PostMapping("/title/{titleId}/highlight")
    public Highlight addHighlight(@PathVariable int titleId, @RequestBody AddHighlightRequest addHighlightRequest,
            @AuthenticationPrincipal Jwt jwt) {

        // TODO (increase title highlights count)

        Title title = titleRepository.findByIdAndUserId(titleId, Auth.currentUserId(jwt))
                .orElseThrow(IllegalArgumentException::new);
        title.setNumberOfHighlights(title.getNumberOfHighlights() + 1);
        titleRepository.save(title);

        return highlightRepository.save(Highlight.builder()
                .title(title)
                .user(User.builder().id(Auth.currentUserId(jwt)).build())
                .highlightText(addHighlightRequest.getHighlightText())
                .build());
    }

    @GetMapping("title/{titleId}/highlight")
    public Set<Highlight> getTitleHighlights(@PathVariable int titleId, @AuthenticationPrincipal Jwt jwt) {
        return highlightRepository.findByTitleIdAndUserId(titleId, Auth.currentUserId(jwt));
    }

    @GetMapping("/highlight")
    public Set<Highlight> getAllHighlights(
            @RequestParam(name = "tagq", required = false, defaultValue = "") String tagQuery,
            @AuthenticationPrincipal Jwt jwt) {
        if (tagQuery == null || tagQuery.equals("")) {
            return highlightRepository.findByUserId(Auth.currentUserId(jwt));
        }
        Tag tag = tagRepository.findByIdAndUserId(tagQuery, Auth.currentUserId(jwt))
                .orElseThrow(() -> new IllegalArgumentException());
        return tag.getHighlights();
    }

    @DeleteMapping("/highlight/{highlightId}")
    @Transactional
    public void removeHighlight(@PathVariable int highlightId, @AuthenticationPrincipal Jwt jwt) {
        highlightRepository.deleteByIdAndUserId(highlightId, Auth.currentUserId(jwt));
    }

    @PatchMapping("/highlight/{highlightId}/favorite")
    public void toggleFavorite(@PathVariable int highlightId, @AuthenticationPrincipal Jwt jwt) {
        Highlight highlight = highlightRepository.findByIdAndUserId(highlightId, Auth.currentUserId(jwt))
                .orElseThrow(() -> new IllegalArgumentException());
        highlight.setFavorite(!highlight.isFavorite());
        highlightRepository.save(highlight);
    }

    @PostMapping("/highlight/{highlightId}/tag")
    public Highlight tagHighlight(@PathVariable int highlightId, @RequestBody TagHighlightRequest tagHighlightRequest,
            @AuthenticationPrincipal Jwt jwt) {
        Highlight highlight = highlightRepository.findByIdAndUserId(highlightId, Auth.currentUserId(jwt))
                .orElseThrow(() -> new IllegalArgumentException());

        if (highlight.getTags().contains(Tag.builder().id(tagHighlightRequest.getTag()).build())) {
            return highlight;
        }

        Tag tag = tagRepository.findByIdAndUserId(tagHighlightRequest.getTag(), Auth.currentUserId(jwt))
                .orElseGet(() -> Tag.builder()
                        .id(tagHighlightRequest.getTag())
                        .user(User.builder().id(Auth.currentUserId(jwt)).build())
                        .build());

        highlight.getTags().add(tag);
        tag.setNumberOfHighlights(tag.getNumberOfHighlights() + 1);

        tagRepository.save(tag);
        return highlightRepository.save(highlight);
    }

    @DeleteMapping("/highlight/{highlightId}/tag")
    public Highlight unTagHighlight(@PathVariable int highlightId,
            @RequestBody TagHighlightRequest tagHighlightRequest, @AuthenticationPrincipal Jwt jwt) {
        Highlight highlight = highlightRepository.findByIdAndUserId(highlightId, Auth.currentUserId(jwt))
                .orElseThrow(() -> new IllegalArgumentException());

        if (!highlight.getTags().contains(Tag.builder().id(tagHighlightRequest.getTag()).build())) {
            return highlight;
        }

        Tag tag = tagRepository.findByIdAndUserId(tagHighlightRequest.getTag(), Auth.currentUserId(jwt))
                .orElseGet(() -> Tag.builder()
                        .id(tagHighlightRequest.getTag())
                        .user(User.builder().id(Auth.currentUserId(jwt)).build())
                        .build());

        highlight.getTags().remove(tag);
        tag.setNumberOfHighlights(tag.getNumberOfHighlights() - 1);

        tagRepository.save(tag);
        return highlightRepository.save(highlight);
    }

}

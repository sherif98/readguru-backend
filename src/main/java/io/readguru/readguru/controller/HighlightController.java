package io.readguru.readguru.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Highlight addHighlight(@PathVariable int titleId, @RequestBody AddHighlightRequest addHighlightRequest) {
        // TODO (increase title highlights count)

        Title title = titleRepository.findById(titleId).orElseThrow(IllegalArgumentException::new);
        title.setNumberOfHighlights(title.getNumberOfHighlights() + 1);
        titleRepository.save(title);

        return highlightRepository.save(Highlight.builder()
                .title(title)
                .user(User.builder().id(1).build())
                .highlightText(addHighlightRequest.getHighlightText())
                .build());
    }

    @GetMapping("title/{titleId}/highlight")
    public List<Highlight> getTitleHighlights(@PathVariable int titleId) {
        return highlightRepository.findByTitleId(titleId);
    }

    @GetMapping("/highlight")
    public List<Highlight> getAllHighlights(
            @RequestParam(name = "titleq", required = false, defaultValue = "") String titleQuery,
            @RequestParam(name = "tagq", required = false, defaultValue = "") String tagQuery,
            @RequestHeader("Authorization") String token) {
        System.out.println(token);
        if (tagQuery.equals("") && titleQuery.equals("")) {
            return highlightRepository.findAll();
        }
        if (!tagQuery.equals("")) {
            Tag tag = tagRepository.findById(tagQuery).orElseThrow(() -> new IllegalArgumentException());
            return tag.getHighlights().stream().toList();
        }
        return highlightRepository.findAll(); // TODO(fetch highlights for a given title)
    }

    @DeleteMapping("/highlight/{highlightId}")
    public void removeHighlight(@PathVariable int highlightId) {
        highlightRepository.deleteById(highlightId);
    }

    @PatchMapping("/highlight/{highlightId}/favorite")
    public void toggleFavorite(@PathVariable int highlightId) {
        Highlight highlight = highlightRepository.findById(highlightId)
                .orElseThrow(() -> new IllegalArgumentException());
        highlight.setFavorite(!highlight.isFavorite());
        highlightRepository.save(highlight);
    }

    @PostMapping("/highlight/{highlightId}/tag")
    public Highlight tagHighlight(@PathVariable int highlightId, @RequestBody TagHighlightRequest tagHighlightRequest) {
        Highlight highlight = highlightRepository.findById(highlightId)
                .orElseThrow(() -> new IllegalArgumentException());

        if (highlight.getTags().contains(Tag.builder().id(tagHighlightRequest.getTag()).build())) {
            return highlight;
        }

        Tag tag = tagRepository.findById(tagHighlightRequest.getTag())
                .orElseGet(() -> Tag.builder()
                        .id(tagHighlightRequest.getTag())
                        .user(User.builder().id(1).build())
                        .build());

        highlight.getTags().add(tag);
        tag.setNumberOfHighlights(tag.getNumberOfHighlights() + 1);

        tagRepository.save(tag);
        return highlightRepository.save(highlight);
    }

    @DeleteMapping("/highlight/{highlightId}/tag")
    public Highlight unTagHighlight(@PathVariable int highlightId,
            @RequestBody TagHighlightRequest tagHighlightRequest) {
        Highlight highlight = highlightRepository.findById(highlightId)
                .orElseThrow(() -> new IllegalArgumentException());

        if (!highlight.getTags().contains(Tag.builder().id(tagHighlightRequest.getTag()).build())) {
            return highlight;
        }

        Tag tag = tagRepository.findById(tagHighlightRequest.getTag())
                .orElseGet(() -> Tag.builder()
                        .id(tagHighlightRequest.getTag())
                        .user(User.builder().id(1).build())
                        .build());

        highlight.getTags().remove(tag);
        tag.setNumberOfHighlights(tag.getNumberOfHighlights() - 1);

        tagRepository.save(tag);
        return highlightRepository.save(highlight);
    }

}

package io.readguru.readguru.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.readguru.readguru.domain.Tag;
import io.readguru.readguru.repository.TagRepository;

@RestController
public class TagController {

    @Autowired
    private TagRepository tagRepository;

    @GetMapping("/tag")
    public List<Tag> listTags() {
        return tagRepository.findAll();
    }

    @DeleteMapping("/tag/{tagId}")
    // TODO tagged highlights not removed
    public void removeTag(@PathVariable String tagId) {
        tagRepository.deleteById(tagId);
    }

}
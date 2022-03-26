package io.readguru.readguru.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.readguru.readguru.domain.Title;
import io.readguru.readguru.domain.User;
import io.readguru.readguru.dto.AddTitleRequest;
import io.readguru.readguru.repository.TitleRepository;

@RestController
public class TitleController {

    @Autowired
    private TitleRepository titleRepository;

    @PostMapping("/title")
    public Title addTitle(@RequestBody AddTitleRequest addTitleRequest) {
        return titleRepository.save(Title.builder()
                .titleName(addTitleRequest.getTitleName())
                .author(addTitleRequest.getAuthor())
                .cover(addTitleRequest.getCover())
                .user(User.builder().id(1).build())
                .build());
    }

    @GetMapping("/title/{titleId}")
    public Title getTitle(@PathVariable int titleId) {
        return titleRepository.findById(titleId).get();
    }

    @GetMapping("/title")
    public List<Title> listTitles() {
        return titleRepository.findAll();
    }

    @DeleteMapping("/title/{titleId}")
    public void removeTitle(@PathVariable int titleId) {
        titleRepository.deleteById(titleId);
    }
}

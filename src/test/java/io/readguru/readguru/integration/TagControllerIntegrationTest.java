package io.readguru.readguru.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import io.readguru.readguru.domain.Tag;
import io.readguru.readguru.domain.User;
import io.readguru.readguru.repository.TagRepository;
import io.readguru.readguru.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integrationtest")
public class TagControllerIntegrationTest {

    private static final String JWT_EMAIL_CLAIM = "email";
    private static final String USER_ID = "123";
    private static final String USER_EMAIL = "email@test.com";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
        tagRepository.deleteAll();
    }

    @Test
    void listTags_noTagsExist_emptyResponse() throws Exception {
        userRepository.save(User.builder().id(USER_ID).email(USER_EMAIL).name(USER_EMAIL).build());

        MvcResult mvcResult = mvc.perform(get("/tag")
                .with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID))))
                .andExpect(status().isOk())
                .andReturn();

        List<Tag> returnedTags = Arrays
                .asList(objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        Tag[].class));

        assertThat(returnedTags).isEmpty();
    }

    @Test
    void listTags_tagsReturnedInResponse() throws Exception {
        User user = userRepository.save(User.builder().id(USER_ID).email(USER_EMAIL).name(USER_EMAIL).build());
        List<Tag> tags = List.of(Tag.builder().id("tag1").user(user).build(),
                Tag.builder().id("tag2").user(user).build());
        tagRepository.saveAll(tags);

        MvcResult mvcResult = mvc.perform(get("/tag")
                .with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID))))
                .andExpect(status().isOk())
                .andReturn();

        List<Tag> returnedTags = Arrays
                .asList(objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        Tag[].class));

        assertThat(returnedTags).isEqualTo(tags);
    }

    @Test
    void deleteTag_tagExists_tagIsDeleted() throws Exception {
        User user = userRepository.save(User.builder().id(USER_ID).email(USER_EMAIL).name(USER_EMAIL).build());
        List<Tag> tags = List.of(Tag.builder().id("tag1").user(user).build(),
                Tag.builder().id("tag2").user(user).build());
        tagRepository.saveAll(tags);

        mvc.perform(delete("/tag/tag1")
                .with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID))))
                .andExpect(status().isOk());

        assertThat(tagRepository.findAll()).containsOnly(Tag.builder().user(user).id("tag2").build());
    }

    @Test()
    void deleteTag_tagDoesNotExist() throws Exception {
        User user = userRepository.save(User.builder().id(USER_ID).email(USER_EMAIL).name(USER_EMAIL).build());
        List<Tag> tags = List.of(Tag.builder().id("tag1").user(user).build(),
                Tag.builder().id("tag2").user(user).build());
        tagRepository.saveAll(tags);

        mvc.perform(delete("/tag/tag3")
                .with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID))))
                .andExpect(status().isNotFound());

        assertThat(tagRepository.findAll()).isEqualTo(tags);
    }

}

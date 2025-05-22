package io.readguru.readguru.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.readguru.readguru.domain.User;
import io.readguru.readguru.model.AddTitleRequest;
import io.readguru.readguru.repository.TitleRepository;
import io.readguru.readguru.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integrationtest")
public class TitleControllerIntegrationTest {

    private static final String JWT_EMAIL_CLAIM = "email";
    private static final String USER_ID = "testUserId123";
    private static final String USER_EMAIL = "titleuser@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TitleRepository titleRepository;

    @AfterEach
    public void cleanUp() {
        titleRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User createAndSaveTestUser() {
        User user = User.builder().id(USER_ID).email(USER_EMAIL).name(USER_EMAIL).build();
        return userRepository.save(user);
    }

    @Test
    void createTitle_shouldCreateAndReturnTitle() throws Exception {
        createAndSaveTestUser();

        AddTitleRequest addTitleRequest = new AddTitleRequest();
        addTitleRequest.setName("New Test Title");
        addTitleRequest.setAuthor("Test Author");
        addTitleRequest.setSource("Test Source");
        addTitleRequest.setSourceUrl("http://example.com/source");
        addTitleRequest.setImgUrl("http://example.com/img.png");

        mockMvc.perform(post("/title")
                        .with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addTitleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Test Title"))
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.source").value("Test Source"))
                .andExpect(jsonPath("$.sourceUrl").value("http://example.com/source"))
                .andExpect(jsonPath("$.imgUrl").value("http://example.com/img.png"))
                .andExpect(jsonPath("$.numberOfHighlights").value(0));

        List<io.readguru.readguru.domain.Title> titlesInDb = titleRepository.findAll();
        assertThat(titlesInDb).hasSize(1);
        assertThat(titlesInDb.get(0).getName()).isEqualTo("New Test Title");
        assertThat(titlesInDb.get(0).getUser().getId()).isEqualTo(USER_ID);
    }

    @Test
    void getTitleById_titleExists_shouldReturnTitle() throws Exception {
        User user = createAndSaveTestUser();
        io.readguru.readguru.domain.Title title = io.readguru.readguru.domain.Title.builder()
                .user(user)
                .name("Existing Title")
                .author("Author")
                .build();
        titleRepository.save(title);

        mockMvc.perform(get("/title/" + title.getId())
                        .with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(title.getId()))
                .andExpect(jsonPath("$.name").value("Existing Title"))
                .andExpect(jsonPath("$.author").value("Author"));
    }

    @Test
    void getTitleById_titleDoesNotExist_shouldReturnNotFound() throws Exception {
        createAndSaveTestUser();

        mockMvc.perform(get("/title/nonExistentTitleId")
                        .with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID))))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTitles_noTitlesExist_shouldReturnEmptyList() throws Exception {
        createAndSaveTestUser();

        MvcResult mvcResult = mockMvc.perform(get("/title")
                        .with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID))))
                .andExpect(status().isOk())
                .andReturn();

        List<io.readguru.readguru.domain.Title> returnedTitles = Arrays.asList(
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), io.readguru.readguru.domain.Title[].class)
        );
        assertThat(returnedTitles).isEmpty();
    }

    @Test
    void getAllTitles_titlesExist_shouldReturnTitleList() throws Exception {
        User user = createAndSaveTestUser();
        io.readguru.readguru.domain.Title title1 = io.readguru.readguru.domain.Title.builder().user(user).name("Title 1").build();
        io.readguru.readguru.domain.Title title2 = io.readguru.readguru.domain.Title.builder().user(user).name("Title 2").build();
        titleRepository.saveAll(List.of(title1, title2));

        MvcResult mvcResult = mockMvc.perform(get("/title")
                        .with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID))))
                .andExpect(status().isOk())
                .andReturn();

        List<io.readguru.readguru.domain.Title> returnedTitles = Arrays.asList(
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), io.readguru.readguru.domain.Title[].class)
        );
        assertThat(returnedTitles).hasSize(2);
        assertThat(returnedTitles).extracting("name").containsExactlyInAnyOrder("Title 1", "Title 2");
    }

    @Test
    void deleteTitle_titleExists_shouldDeleteTitle() throws Exception {
        User user = createAndSaveTestUser();
        io.readguru.readguru.domain.Title titleToDelete = io.readguru.readguru.domain.Title.builder()
                .user(user)
                .name("Title to Delete")
                .build();
        titleRepository.save(titleToDelete);
        String titleId = titleToDelete.getId();

        mockMvc.perform(delete("/title/" + titleId)
                        .with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID))))
                .andExpect(status().isOk()); // Assuming 200 OK for successful deletion as per controller

        assertThat(titleRepository.findById(titleId)).isEmpty();
    }
    
    @Test
    void deleteTitle_titleDoesNotExist_shouldReturnNotFound() throws Exception {
        createAndSaveTestUser();

        mockMvc.perform(delete("/title/nonExistentTitleId")
                        .with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID))))
                .andExpect(status().isNotFound());
    }
}

package io.readguru.readguru.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.readguru.readguru.repository.HighlightRepository;
import io.readguru.readguru.repository.TagRepository;
import io.readguru.readguru.repository.TitleRepository;
import io.readguru.readguru.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integrationtest")
public class HighlightControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TitleRepository titleRepository;

    @Autowired
    private HighlightRepository highlightRepository;

    @Autowired
    private TagRepository tagRepository;

    // Define necessary constants for user ID and email
    private static final String USER_ID = "testUserId";
    private static final String USER_EMAIL = "testuser@example.com";
    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Logic to generate a JWT token for the test user
        // This is a simplified representation. In a real scenario, use a proper JWT library.
        jwtToken = "Bearer " + generateToken(USER_ID, USER_EMAIL);
    }

    private String generateToken(String userId, String email) {
        // In a real application, this would involve a JWT library like jjwt
        // For testing purposes, we can create a simple token structure
        // This is NOT a secure way to generate JWTs for production
        return "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlckBleGFtcGxlLmNvbSIsImVtYWlsIjoidGVzdHVzZXJAZXhhbXBsZS5jb20iLCJ1c2VySWQiOiJ0ZXN0VXNlcklkIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    }

    @AfterEach
    void tearDown() {
        highlightRepository.deleteAll();
        tagRepository.deleteAll();
        titleRepository.deleteAll();
        userRepository.deleteAll();
    }

    // Test methods will be added here
    
    @Test
    void createHighlight_shouldCreateHighlightAndIncrementTitleHighlightCount() throws Exception {
        // Create and save a user
        io.readguru.readguru.entity.User user = new io.readguru.readguru.entity.User();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);
        user.setUsername("testuser");
        userRepository.save(user);

        // Create and save a title
        io.readguru.readguru.entity.Title title = new io.readguru.readguru.entity.Title();
        title.setUser(user);
        title.setName("Test Title");
        title.setNumberOfHighlights(0);
        titleRepository.save(title);

        // Prepare highlight creation request
        io.readguru.readguru.model.HighlightCreationRequest request = new io.readguru.readguru.model.HighlightCreationRequest();
        request.setText("This is a test highlight.");
        request.setNote("Test note.");

        // Perform POST request
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/title/" + title.getId() + "/highlight")
                        .header("Authorization", jwtToken)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.text").value("This is a test highlight."))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.note").value("Test note."));

        // Assert highlight is saved
        java.util.List<io.readguru.readguru.entity.Highlight> highlights = highlightRepository.findAll();
        org.assertj.core.api.Assertions.assertThat(highlights).hasSize(1);
        org.assertj.core.api.Assertions.assertThat(highlights.get(0).getText()).isEqualTo("This is a test highlight.");
        org.assertj.core.api.Assertions.assertThat(highlights.get(0).getNote()).isEqualTo("Test note.");
        org.assertj.core.api.Assertions.assertThat(highlights.get(0).getTitle().getId()).isEqualTo(title.getId());

        // Assert title highlight count is updated
        io.readguru.readguru.entity.Title updatedTitle = titleRepository.findById(title.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(updatedTitle.getNumberOfHighlights()).isEqualTo(1);
    }

    @Test
    void getHighlightsByTitle_shouldReturnHighlightsForTitle() throws Exception {
        // Create and save a user
        io.readguru.readguru.entity.User user = new io.readguru.readguru.entity.User();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);
        user.setUsername("testuser");
        userRepository.save(user);

        // Create and save a title
        io.readguru.readguru.entity.Title title = new io.readguru.readguru.entity.Title();
        title.setUser(user);
        title.setName("Test Title");
        titleRepository.save(title);

        // Create and save a highlight
        io.readguru.readguru.entity.Highlight highlight = new io.readguru.readguru.entity.Highlight();
        highlight.setTitle(title);
        highlight.setUser(user);
        highlight.setText("Test highlight text");
        highlightRepository.save(highlight);

        // Perform GET request
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/title/" + title.getId() + "/highlight")
                        .header("Authorization", jwtToken)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].text").value("Test highlight text"));
    }

    @Test
    void getAllHighlightsByUser_shouldReturnAllHighlightsForUser() throws Exception {
        // Create and save a user
        io.readguru.readguru.entity.User user = new io.readguru.readguru.entity.User();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);
        user.setUsername("testuser");
        userRepository.save(user);

        // Create and save a title
        io.readguru.readguru.entity.Title title = new io.readguru.readguru.entity.Title();
        title.setUser(user);
        title.setName("Test Title");
        titleRepository.save(title);

        // Create and save two highlights
        io.readguru.readguru.entity.Highlight highlight1 = new io.readguru.readguru.entity.Highlight();
        highlight1.setTitle(title);
        highlight1.setUser(user);
        highlight1.setText("Highlight 1 text");
        highlightRepository.save(highlight1);

        io.readguru.readguru.entity.Highlight highlight2 = new io.readguru.readguru.entity.Highlight();
        highlight2.setTitle(title);
        highlight2.setUser(user);
        highlight2.setText("Highlight 2 text");
        highlightRepository.save(highlight2);

        // Perform GET request
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/highlight")
                        .header("Authorization", jwtToken)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].text").value("Highlight 1 text"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[1].text").value("Highlight 2 text"));
    }

    @Test
    void getHighlightsByTag_shouldReturnFilteredHighlights() throws Exception {
        // Create and save a user
        io.readguru.readguru.entity.User user = new io.readguru.readguru.entity.User();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);
        user.setUsername("testuser");
        userRepository.save(user);

        // Create and save a title
        io.readguru.readguru.entity.Title title = new io.readguru.readguru.entity.Title();
        title.setUser(user);
        title.setName("Test Title");
        titleRepository.save(title);

        // Create and save a tag
        io.readguru.readguru.entity.Tag tag = new io.readguru.readguru.entity.Tag();
        tag.setName("testtag");
        tag.setUser(user);
        tagRepository.save(tag);

        // Create and save highlight 1 (with tag)
        io.readguru.readguru.entity.Highlight highlight1 = new io.readguru.readguru.entity.Highlight();
        highlight1.setTitle(title);
        highlight1.setUser(user);
        highlight1.setText("Highlight 1 with tag");
        highlight1.getTags().add(tag);
        highlightRepository.save(highlight1);
        // Update tag's highlight count
        tag.setNumberOfHighlights(1);
        tagRepository.save(tag);


        // Create and save highlight 2 (without tag)
        io.readguru.readguru.entity.Highlight highlight2 = new io.readguru.readguru.entity.Highlight();
        highlight2.setTitle(title);
        highlight2.setUser(user);
        highlight2.setText("Highlight 2 without tag");
        highlightRepository.save(highlight2);

        // Perform GET request with tagq parameter
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/highlight")
                        .param("tagq", "testtag")
                        .header("Authorization", jwtToken)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].text").value("Highlight 1 with tag"));
    }

    @Test
    void deleteHighlight_shouldDeleteHighlightAndDecrementTitleHighlightCount() throws Exception {
        // Create and save a user
        io.readguru.readguru.entity.User user = new io.readguru.readguru.entity.User();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);
        user.setUsername("testuser");
        userRepository.save(user);

        // Create and save a title
        io.readguru.readguru.entity.Title title = new io.readguru.readguru.entity.Title();
        title.setUser(user);
        title.setName("Test Title");
        title.setNumberOfHighlights(1); // Set initial count
        titleRepository.save(title);

        // Create and save a highlight
        io.readguru.readguru.entity.Highlight highlight = new io.readguru.readguru.entity.Highlight();
        highlight.setTitle(title);
        highlight.setUser(user);
        highlight.setText("Test highlight to delete");
        highlightRepository.save(highlight);

        // Perform DELETE request
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/highlight/" + highlight.getId())
                        .header("Authorization", jwtToken)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNoContent());

        // Assert highlight is deleted
        org.assertj.core.api.Assertions.assertThat(highlightRepository.findById(highlight.getId())).isEmpty();

        // Assert title highlight count is updated
        io.readguru.readguru.entity.Title updatedTitle = titleRepository.findById(title.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(updatedTitle.getNumberOfHighlights()).isEqualTo(0);
    }

    @Test
    void toggleFavoriteHighlight_shouldUpdateFavoriteStatus() throws Exception {
        // Create and save a user
        io.readguru.readguru.entity.User user = new io.readguru.readguru.entity.User();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);
        user.setUsername("testuser");
        userRepository.save(user);

        // Create and save a title
        io.readguru.readguru.entity.Title title = new io.readguru.readguru.entity.Title();
        title.setUser(user);
        title.setName("Test Title");
        titleRepository.save(title);

        // Create and save a highlight
        io.readguru.readguru.entity.Highlight highlight = new io.readguru.readguru.entity.Highlight();
        highlight.setTitle(title);
        highlight.setUser(user);
        highlight.setText("Test highlight for favorite toggle");
        highlight.setFavorite(false);
        highlightRepository.save(highlight);

        // Perform PATCH request to set favorite to true
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/highlight/" + highlight.getId() + "/favorite")
                        .header("Authorization", jwtToken)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.isFavorite").value(true));

        // Assert highlight isFavorite is true in DB
        io.readguru.readguru.entity.Highlight updatedHighlight = highlightRepository.findById(highlight.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(updatedHighlight.isFavorite()).isTrue();

        // Perform PATCH request to set favorite to false
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/highlight/" + highlight.getId() + "/favorite")
                        .header("Authorization", jwtToken)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.isFavorite").value(false));

        // Assert highlight isFavorite is false in DB
        updatedHighlight = highlightRepository.findById(highlight.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(updatedHighlight.isFavorite()).isFalse();
    }

    @Test
    void addExistingTagToHighlight_shouldAddTagAndIncrementTagHighlightCount() throws Exception {
        // Create and save a user
        io.readguru.readguru.entity.User user = new io.readguru.readguru.entity.User();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);
        user.setUsername("testuser");
        userRepository.save(user);

        // Create and save a title
        io.readguru.readguru.entity.Title title = new io.readguru.readguru.entity.Title();
        title.setUser(user);
        title.setName("Test Title");
        titleRepository.save(title);

        // Create and save a highlight
        io.readguru.readguru.entity.Highlight highlight = new io.readguru.readguru.entity.Highlight();
        highlight.setTitle(title);
        highlight.setUser(user);
        highlight.setText("Test highlight for adding tag");
        highlightRepository.save(highlight);

        // Create and save an existing tag
        io.readguru.readguru.entity.Tag existingTag = new io.readguru.readguru.entity.Tag();
        existingTag.setName("existingtag");
        existingTag.setUser(user);
        existingTag.setNumberOfHighlights(0);
        tagRepository.save(existingTag);

        // Prepare TagRequest
        io.readguru.readguru.model.TagRequest tagRequest = new io.readguru.readguru.model.TagRequest();
        tagRequest.setName("existingtag");

        // Perform POST request
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/highlight/" + highlight.getId() + "/tag")
                        .header("Authorization", jwtToken)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.tags[?(@.name=='existingtag')]").exists());

        // Assert highlight has the tag
        io.readguru.readguru.entity.Highlight updatedHighlight = highlightRepository.findById(highlight.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(updatedHighlight.getTags()).anyMatch(t -> t.getName().equals("existingtag"));

        // Assert tag highlight count is updated
        io.readguru.readguru.entity.Tag updatedTag = tagRepository.findByNameAndUserId("existingtag", USER_ID).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(updatedTag.getNumberOfHighlights()).isEqualTo(1);
    }

    @Test
    void addNewTagToHighlight_shouldCreateTagAndAddItToHighlightAndIncrementTagHighlightCount() throws Exception {
        // Create and save a user
        io.readguru.readguru.entity.User user = new io.readguru.readguru.entity.User();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);
        user.setUsername("testuser");
        userRepository.save(user);

        // Create and save a title
        io.readguru.readguru.entity.Title title = new io.readguru.readguru.entity.Title();
        title.setUser(user);
        title.setName("Test Title");
        titleRepository.save(title);

        // Create and save a highlight
        io.readguru.readguru.entity.Highlight highlight = new io.readguru.readguru.entity.Highlight();
        highlight.setTitle(title);
        highlight.setUser(user);
        highlight.setText("Test highlight for adding new tag");
        highlightRepository.save(highlight);

        // Prepare TagRequest for a new tag
        io.readguru.readguru.model.TagRequest tagRequest = new io.readguru.readguru.model.TagRequest();
        tagRequest.setName("newtag");

        // Perform POST request
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/highlight/" + highlight.getId() + "/tag")
                        .header("Authorization", jwtToken)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.tags[?(@.name=='newtag')]").exists());

        // Assert new tag is created
        io.readguru.readguru.entity.Tag newTag = tagRepository.findByNameAndUserId("newtag", USER_ID).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(newTag).isNotNull();
        org.assertj.core.api.Assertions.assertThat(newTag.getName()).isEqualTo("newtag");


        // Assert highlight has the new tag
        io.readguru.readguru.entity.Highlight updatedHighlight = highlightRepository.findById(highlight.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(updatedHighlight.getTags()).anyMatch(t -> t.getName().equals("newtag"));

        // Assert tag highlight count is updated
        org.assertj.core.api.Assertions.assertThat(newTag.getNumberOfHighlights()).isEqualTo(1);
    }

    @Test
    void removeTagFromHighlight_shouldRemoveTagAndDecrementTagHighlightCount() throws Exception {
        // Create and save a user
        io.readguru.readguru.entity.User user = new io.readguru.readguru.entity.User();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);
        user.setUsername("testuser");
        userRepository.save(user);

        // Create and save a title
        io.readguru.readguru.entity.Title title = new io.readguru.readguru.entity.Title();
        title.setUser(user);
        title.setName("Test Title");
        titleRepository.save(title);

        // Create and save a tag
        io.readguru.readguru.entity.Tag tagToRemove = new io.readguru.readguru.entity.Tag();
        tagToRemove.setName("tagtoremove");
        tagToRemove.setUser(user);
        tagToRemove.setNumberOfHighlights(1); // Initial count before removal
        tagRepository.save(tagToRemove);

        // Create and save a highlight with the tag
        io.readguru.readguru.entity.Highlight highlight = new io.readguru.readguru.entity.Highlight();
        highlight.setTitle(title);
        highlight.setUser(user);
        highlight.setText("Test highlight for removing tag");
        highlight.getTags().add(tagToRemove);
        highlightRepository.save(highlight);


        // Perform DELETE request
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/highlight/" + highlight.getId() + "/tag")
                        .header("Authorization", jwtToken)
                        .param("tag_name", "tagtoremove")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());

        // Assert tag is removed from highlight
        io.readguru.readguru.entity.Highlight updatedHighlight = highlightRepository.findById(highlight.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(updatedHighlight.getTags()).noneMatch(t -> t.getName().equals("tagtoremove"));

        // Assert tag highlight count is updated
        io.readguru.readguru.entity.Tag updatedTag = tagRepository.findByNameAndUserId("tagtoremove", USER_ID).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(updatedTag.getNumberOfHighlights()).isEqualTo(0);
    }
}

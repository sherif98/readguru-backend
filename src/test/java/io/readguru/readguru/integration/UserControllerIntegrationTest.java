package io.readguru.readguru.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import io.readguru.readguru.domain.User;
import io.readguru.readguru.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integrationtest")
class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mvc;

	private static final String JWT_EMAIL_CLAIM = "email";
	private static final String USER_ID = "123";
	private static final String USER_EMAIL = "email@test.com";

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@AfterEach
	public void cleanUp() {
		userRepository.deleteAll();
	}

	@BeforeEach
	public void setup() {
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
	}

	@Test
	void registerNewUser_onlyOneUserIsCreated() throws Exception {
		mvc.perform(post("/user")
				.with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID))))
				.andExpect(status().isOk());

		assertThat(userRepository.findAll().size()).isEqualTo(1);
	}

	@Test
	void registerNewUser_userCreatedCorrectly() throws Exception {
		mvc.perform(post("/user")
				.with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID))))
				.andExpect(status().isOk());

		Optional<User> user = userRepository.findById(USER_ID);
		assertThat(user.isPresent()).isTrue();
		assertThat(user.get().getId()).isEqualTo(USER_ID);
		assertThat(user.get().getEmail()).isEqualTo(USER_EMAIL);
		assertThat(user.get().getName()).isEqualTo(USER_EMAIL);
		assertThat(user.get().getDailyHighlightReviewCount()).isEqualTo(User.DEFAULT_DAILY_HIGHLIGHT_REVIEW_COUNT);
		assertThat(user.get().getCreatedAt()).isCloseToUtcNow(within(1, ChronoUnit.MINUTES));
		assertThat(user.get().getUpdatedAt()).isCloseToUtcNow(within(1, ChronoUnit.MINUTES));
		assertThat(user.get().getHighlightsReviewTime()).isCloseToUtcNow(within(1, ChronoUnit.MINUTES));
	}

	@Test
	void registerNewUser_userReturnedInResponse() throws Exception {
		MvcResult mvcResult = mvc.perform(post("/user")
				.with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID))))
				.andExpect(status().isOk())
				.andReturn();

		User expectedUser = userRepository.findById(USER_ID).get();
		String actualResponseBody = mvcResult.getResponse().getContentAsString();

		assertThat(actualResponseBody).isEqualTo(objectMapper.writeValueAsString(expectedUser));
	}

	@Test
	void registerExistingUser_noUserIsCreated() throws Exception {
		userRepository.save(User.builder().id(USER_ID).email(USER_EMAIL).name(USER_EMAIL).build());

		mvc.perform(post("/user")
				.with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID))))
				.andExpect(status().isOk());

		assertThat(userRepository.findAll().size()).isEqualTo(1);
	}

	@Test
	void registerExistingUser_userReturnedInResponse() throws Exception {
		User existingUser = userRepository.save(User.builder().id(USER_ID).email(USER_EMAIL).name(USER_EMAIL).build());

		MvcResult mvcResult = mvc.perform(post("/user")
				.with(jwt().jwt(x -> x.claim(JWT_EMAIL_CLAIM, USER_EMAIL).subject(USER_ID))))
				.andExpect(status().isOk())
				.andReturn();

		String actualResponseBody = mvcResult.getResponse().getContentAsString();

		assertThat(actualResponseBody).isEqualTo(objectMapper.writeValueAsString(existingUser));
	}
}

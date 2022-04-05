package io.readguru.readguru;

import java.util.Set;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.readguru.readguru.domain.Highlight;
import io.readguru.readguru.domain.Tag;
import io.readguru.readguru.domain.Title;
import io.readguru.readguru.domain.User;
import io.readguru.readguru.repository.HighlightRepository;
import io.readguru.readguru.repository.TagRepository;
import io.readguru.readguru.repository.TitleRepository;
import io.readguru.readguru.repository.UserRepository;

@SpringBootApplication
public class ReadguruApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReadguruApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// Setting Spring Boot SetTimeZone
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
}

@Profile("!integrationtest")
@Component
class ApplicationRunnerTaskExecutor implements ApplicationRunner {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TitleRepository titleRepository;
	@Autowired
	private HighlightRepository highlightRepository;

	@Autowired
	private TagRepository tagRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (userRepository.findById("FKvF4zGBivXGAGlpoV8d7BJSVoz2").isPresent()) {
			return;
		}
		User user = userRepository.save(User.builder()
				.id("FKvF4zGBivXGAGlpoV8d7BJSVoz2")
				.name("sherif")
				.email("sherif@test.com")
				.build());

		Tag epicTag = tagRepository.save(Tag.builder().user(user).id("epic").numberOfHighlights(2).build());
		Tag lifeTag = tagRepository.save(Tag.builder().user(user).id("life").numberOfHighlights(1).build());

		Title title1 = titleRepository
				.save(Title.builder().user(user).titleName("Atomic Habits").numberOfHighlights(2).author("James Clear")
						.cover("").build());
		Title title2 = titleRepository
				.save(Title.builder().user(user).titleName("Deep Work").numberOfHighlights(1).author("Cal Newport")
						.cover("").build());

		highlightRepository.save(Highlight.builder().user(user).title(title1)
				.tags(Set.of(epicTag))
				.highlightText("Remove the mental candy from the environment").build());
		highlightRepository.save(Highlight.builder().user(user).title(title1)
				.highlightText("You need to standardize before you can optimize").build());
		highlightRepository.save(Highlight.builder().user(user).title(title2)
				.isFavorite(true)
				.tags(Set.of(epicTag, lifeTag))
				.highlightText("The idle mind is the devil's workshop").build());

	}
}
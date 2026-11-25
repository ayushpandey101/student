package dev.StudentApp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import dev.StudentApp.entity.AppUser;
import dev.StudentApp.entity.Student;
import dev.StudentApp.repository.AppUserRepository;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:studentdb",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password="
})
class StudentAppApplicationTests {

	@Autowired
	private Validator validator;

	@Autowired
	private WebApplicationContext applicationContext;

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {
	}

	@Test
	void loginPageIsAvailableWithoutAuthentication() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
				.apply(springSecurity())
				.build();

		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"));
		mockMvc.perform(get("/signup"))
				.andExpect(status().isOk())
				.andExpect(view().name("signup"));
	}

	@Test
	void signupPersistsHashedAccountAndSignsUserIn() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
				.apply(springSecurity())
				.build();
		String suffix = UUID.randomUUID().toString().substring(0, 8);
		String username = "signup-" + suffix;
		String email = username + "@example.com";
		String password = "SignupTestPassword-123!";

		var signupResult = mockMvc.perform(post("/signup")
						.with(csrf())
						.param("username", username)
						.param("email", email)
						.param("password", password)
						.param("confirmPassword", password))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andReturn();

		AppUser account = appUserRepository.findByUsername(username).orElseThrow();
		assertNotEquals(password, account.getPasswordHash());
		assertTrue(passwordEncoder.matches(password, account.getPasswordHash()));
		assertEquals("USER", account.getRole());

		MockHttpSession session = (MockHttpSession) signupResult.getRequest().getSession(false);
		assertNotNull(session);
		mockMvc.perform(get("/").session(session))
				.andExpect(status().isOk())
				.andExpect(view().name("students"));

		mockMvc.perform(post("/signup")
						.with(csrf())
						.param("username", username.toUpperCase())
						.param("email", "different-" + email)
						.param("password", password)
						.param("confirmPassword", password))
				.andExpect(status().isOk())
				.andExpect(view().name("signup"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(
					"That username is already registered")));
	}

	@Test
	void studentValidationRejectsInvalidFields() {
		Student student = new Student();
		student.setName("A1");
		student.setEmail("invalid");
		student.setCourse("Unknown");

		Set<String> invalidFields = validator.validate(student).stream()
				.map(violation -> violation.getPropertyPath().toString())
				.collect(Collectors.toSet());

		assertTrue(invalidFields.containsAll(Set.of("name", "email", "course")));
	}

	@Test
	void studentValidationAcceptsValidFields() {
		Student student = new Student();
		student.setName("Asha Pandey");
		student.setEmail("asha@example.com");
		student.setCourse("Java");

		assertFalse(validator.validate(student).iterator().hasNext());
	}
}

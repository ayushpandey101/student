package dev.StudentApp.service;

import dev.StudentApp.entity.AppUser;
import dev.StudentApp.form.SignupForm;
import dev.StudentApp.repository.AppUserRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser register(SignupForm form) {
        String username = form.getUsername().trim().toLowerCase(Locale.ROOT);
        String email = form.getEmail().trim().toLowerCase(Locale.ROOT);

        if (appUserRepository.existsByUsername(username)) {
            throw new DuplicateAccountException("username");
        }
        if (appUserRepository.existsByEmail(email)) {
            throw new DuplicateAccountException("email");
        }

        AppUser account = new AppUser(
            username,
            email,
            passwordEncoder.encode(form.getPassword())
        );
        return appUserRepository.save(account);
    }
}

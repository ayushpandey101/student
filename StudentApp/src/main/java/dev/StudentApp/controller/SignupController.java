package dev.StudentApp.controller;

import dev.StudentApp.form.SignupForm;
import dev.StudentApp.service.DuplicateAccountException;
import dev.StudentApp.service.SignupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/signup")
public class SignupController {

    private final SignupService signupService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public SignupController(
            SignupService signupService,
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository) {
        this.signupService = signupService;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @GetMapping
    public String showSignupForm(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "signup";
    }

    @PostMapping
    public String signup(
            @Valid @ModelAttribute("signupForm") SignupForm form,
            BindingResult result,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (form.getPassword() != null
                && !form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "signup.password.mismatch",
                "Passwords do not match");
        }

        if (result.hasErrors()) {
            return "signup";
        }

        try {
            var account = signupService.register(form);
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    account.getUsername(),
                    form.getPassword()
                )
            );

            if (request.getSession(false) != null) {
                request.changeSessionId();
            }

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
        } catch (DuplicateAccountException exception) {
            result.rejectValue(
                exception.getField(),
                "signup.duplicate",
                "That " + exception.getField() + " is already registered"
            );
            return "signup";
        }

        return "redirect:/";
    }
}

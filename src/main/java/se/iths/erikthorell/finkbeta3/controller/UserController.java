package se.iths.erikthorell.finkbeta3.controller;

import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import se.iths.erikthorell.finkbeta3.dto.RegisterUserForm;
import se.iths.erikthorell.finkbeta3.model.Role;
import se.iths.erikthorell.finkbeta3.model.User;
import se.iths.erikthorell.finkbeta3.repository.UserRepository;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterForm(@ModelAttribute("form") RegisterUserForm form) {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("form") RegisterUserForm form,
            BindingResult bindingResult
    ) {
        if (userRepository.existsByUsername(form.getUsername())) {
            bindingResult.rejectValue("username", "username.exists", "Användarnamnet används redan");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        User user = new User();
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);

        return "redirect:/";
    }
}
package se.iths.erikthorell.finkbeta3.controller;

import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import se.iths.erikthorell.finkbeta3.dto.RegisterUserForm;
import se.iths.erikthorell.finkbeta3.model.Role;
import se.iths.erikthorell.finkbeta3.model.User;
import se.iths.erikthorell.finkbeta3.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.UUID;

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

    @PostMapping("/profile/upload")
    public String uploadProfileImage(
            @RequestParam("image") MultipartFile image,
            Principal principal
    ) throws IOException {

        if (principal == null) {
            return "redirect:/";
        }

        if (image.isEmpty()) {
            return "redirect:/home";
        }

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        String uploadDir = "uploads";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = image.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFileName = UUID.randomUUID() + extension;
        Path filePath = uploadPath.resolve(newFileName);

        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        user.setProfileImageName(newFileName);
        userRepository.save(user);

        return "redirect:/home";
    }
}
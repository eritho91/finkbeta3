package se.iths.erikthorell.finkbeta3.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import se.iths.erikthorell.finkbeta3.dto.BirdPostForm;
import se.iths.erikthorell.finkbeta3.model.BirdPost;
import se.iths.erikthorell.finkbeta3.model.User;
import se.iths.erikthorell.finkbeta3.repository.BirdPostRepository;
import se.iths.erikthorell.finkbeta3.repository.UserRepository;
import se.iths.erikthorell.finkbeta3.service.BirdImageResult;
import se.iths.erikthorell.finkbeta3.service.BirdImageService;
import se.iths.erikthorell.textformatter.TextFormatter;


import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/birds")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class BirdController {

    private final BirdPostRepository birdPostRepository;
    private final UserRepository userRepository;
    private final BirdImageService birdImageService;
    private final TextFormatter textFormatter;

    public BirdController(
            BirdPostRepository birdPostRepository,
            UserRepository userRepository,
            BirdImageService birdImageService, TextFormatter textFormatter
    ) {
        this.birdPostRepository = birdPostRepository;
        this.userRepository = userRepository;
        this.birdImageService = birdImageService;
        this.textFormatter = textFormatter;
    }

    @GetMapping
    public String listBirds(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        List<BirdPost> birds = birdPostRepository.findByUserOrderByObservedAtDesc(user);

        model.addAttribute("birds", birds);
        model.addAttribute("user", user);

        return "birds";
    }

    @GetMapping("/new")
    public String showNewBirdForm(Model model) {
        model.addAttribute("form", new BirdPostForm());
        return "newBirdPost";
    }

    @PostMapping
    public String createBird(
            @Valid @ModelAttribute("form") BirdPostForm form,
            BindingResult bindingResult,
            Principal principal
    ) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        if (bindingResult.hasErrors()) {
            return "newBirdPost";
        }

        BirdPost birdPost = new BirdPost();
        birdPost.setSpecies(form.getSpecies());
        birdPost.setLocation(form.getLocation());
        birdPost.setObservedAt(form.getObservedAt());
        birdPost.setUser(user);

        BirdImageResult imageResult = birdImageService.fetchBirdImage(form.getSpecies());

        if (imageResult != null) {
            birdPost.setImageUrl(imageResult.getImageUrl());
            birdPost.setImageAttribution(imageResult.getAttribution());
            birdPost.setImageSource(imageResult.getSource());
        }

        birdPostRepository.save(birdPost);

        return "redirect:/birds";
    }

    @GetMapping("/edit/{id}")
    public String showEditBirdForm(@PathVariable Long id, Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        BirdPost birdPost = birdPostRepository.findByIdAndUser(id, user).orElseThrow();

        BirdPostForm form = new BirdPostForm();
        form.setSpecies(birdPost.getSpecies());
        form.setLocation(birdPost.getLocation());
        form.setObservedAt(birdPost.getObservedAt());

        model.addAttribute("form", form);
        model.addAttribute("birdId", birdPost.getId());

        return "editBirdPost";
    }

    @PostMapping("/edit/{id}")
    public String updateBird(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") BirdPostForm form,
            BindingResult bindingResult,
            Model model,
            Principal principal
    ) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        BirdPost existingBird = birdPostRepository.findByIdAndUser(id, user).orElseThrow();

        if (bindingResult.hasErrors()) {
            model.addAttribute("birdId", id);
            return "editBirdPost";
        }

        boolean speciesChanged = !existingBird.getSpecies().equalsIgnoreCase(form.getSpecies());

        existingBird.setSpecies(form.getSpecies());
        existingBird.setLocation(form.getLocation());
        existingBird.setObservedAt(form.getObservedAt());

        if (speciesChanged) {
            BirdImageResult imageResult = birdImageService.fetchBirdImage(form.getSpecies());

            if (imageResult != null) {
                existingBird.setImageUrl(imageResult.getImageUrl());
                existingBird.setImageAttribution(imageResult.getAttribution());
                existingBird.setImageSource(imageResult.getSource());
            } else {
                existingBird.setImageUrl(null);
                existingBird.setImageAttribution(null);
                existingBird.setImageSource(null);
            }
        }

        birdPostRepository.save(existingBird);

        return "redirect:/birds";
    }

    @PostMapping("/delete/{id}")
    public String deleteBird(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        BirdPost birdPost = birdPostRepository.findByIdAndUser(id, user).orElseThrow();

        birdPostRepository.delete(birdPost);

        return "redirect:/birds";
    }
}
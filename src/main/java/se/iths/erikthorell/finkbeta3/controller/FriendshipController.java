package se.iths.erikthorell.finkbeta3.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.iths.erikthorell.finkbeta3.model.Friendship;
import se.iths.erikthorell.finkbeta3.model.FriendshipStatus;
import se.iths.erikthorell.finkbeta3.model.User;
import se.iths.erikthorell.finkbeta3.repository.FriendshipRepository;
import se.iths.erikthorell.finkbeta3.repository.UserRepository;

import java.security.Principal;

@Controller
@RequestMapping("/friends")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class FriendshipController {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendshipController(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/request/{receiverId}")
    public String sendFriendRequest(@PathVariable Long receiverId, Principal principal) {
        User sender = userRepository.findByUsername(principal.getName()).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();

        if (sender.getId().equals(receiver.getId())) {
            return "redirect:/home";
        }

        boolean alreadyExists = friendshipRepository.existsBySenderAndReceiverOrSenderAndReceiver(
                sender, receiver, receiver, sender
        );

        if (alreadyExists) {
            return "redirect:/home";
        }

        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setReceiver(receiver);
        friendship.setStatus(FriendshipStatus.PENDING);

        friendshipRepository.save(friendship);

        return "redirect:/home";
    }

    @PostMapping("/accept/{id}")
    public String acceptFriendRequest(@PathVariable Long id, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow();

        Friendship friendship = friendshipRepository.findById(id).orElseThrow();

        if (!friendship.getReceiver().getId().equals(currentUser.getId())) {
            return "redirect:/home";
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);

        return "redirect:/home";
    }
}
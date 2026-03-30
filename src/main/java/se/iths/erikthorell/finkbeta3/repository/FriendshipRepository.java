package se.iths.erikthorell.finkbeta3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.iths.erikthorell.finkbeta3.model.Friendship;
import se.iths.erikthorell.finkbeta3.model.FriendshipStatus;
import se.iths.erikthorell.finkbeta3.model.User;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    List<Friendship> findByReceiverAndStatus(User receiver, FriendshipStatus status);

    List<Friendship> findBySenderOrReceiverAndStatus(User sender, User receiver, FriendshipStatus status);

    Optional<Friendship> findBySenderAndReceiver(User sender, User receiver);

    boolean existsBySenderAndReceiverOrSenderAndReceiver(
            User sender1, User receiver1,
            User sender2, User receiver2
    );
}
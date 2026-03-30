package se.iths.erikthorell.finkbeta3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.iths.erikthorell.finkbeta3.model.BirdPost;
import se.iths.erikthorell.finkbeta3.model.User;

import java.util.List;
import java.util.Optional;

public interface BirdPostRepository extends JpaRepository<BirdPost, Long> {

    List<BirdPost> findByUserOrderByObservedAtDesc(User user);

    Optional<BirdPost> findByIdAndUser(Long id, User user);
}
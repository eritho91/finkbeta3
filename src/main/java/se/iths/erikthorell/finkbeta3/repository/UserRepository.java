package se.iths.erikthorell.finkbeta3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.iths.erikthorell.finkbeta3.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findByUsernameContainingIgnoreCaseOrderByUsernameAsc(String username);
}
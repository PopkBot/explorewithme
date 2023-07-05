package main.user.repository;

import main.user.model.User;
import main.user.projections.UserIdProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<UserIdProjection> findByEmail(String email);

    Page<User> findAllByIdIn(List<Long> id, Pageable pageable);
}

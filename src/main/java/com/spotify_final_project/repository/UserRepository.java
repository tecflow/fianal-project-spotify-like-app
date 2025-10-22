package com.spotify_final_project.repository;

import com.spotify_final_project.dto.user.UserSummary;
import com.spotify_final_project.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    List<UserSummary> findAllProjectedBy(Pageable pageable);

    Optional<Object> findByEmail(String email);
}

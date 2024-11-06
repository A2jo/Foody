package com.my.foody.domain.user.repo;

import com.my.foody.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    @Query("select u from User u where u.id = :userId and u.isDeleted = false")
    Optional<User> findActivateUser(@Param(value = "userId")Long userId);
}

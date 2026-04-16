package com.mygordienko.spring.security.examples.oauth2server.oauth.port;

import com.mygordienko.spring.security.examples.oauth2server.userauth.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
package com.mygordienko.spring.security.examples.oauth2server.oauth.port;

import com.mygordienko.spring.security.examples.oauth2server.oauth.StoredClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoredClientRepository extends JpaRepository<StoredClient, Long> {
    Optional<StoredClient> findByClientId(String clientId);
    void deleteByClientId(String clientId);
}
package com.mygordienko.spring.security.examples.oauth2server.oauth.adapter;

import com.mygordienko.spring.security.examples.oauth2server.userauth.AppUser;
import com.mygordienko.spring.security.examples.oauth2server.userauth.Role;
import com.mygordienko.spring.security.examples.oauth2server.oauth.port.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        createUserIfAbsent("admin", "admin", Role.ROLE_ADMIN);
        createUserIfAbsent("user",  "user",  Role.ROLE_USER);
    }

    private void createUserIfAbsent(String username, String rawPassword, Role role) {
        if (userRepository.findByUsername(username).isEmpty()) {
            var user = new AppUser();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            userRepository.save(user);
        }
    }
}

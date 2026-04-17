package com.mygordienko.spring.security.examples.oauth2server.userauth.adapter;

import com.mygordienko.spring.security.examples.oauth2server.userauth.AppUser;
import com.mygordienko.spring.security.examples.oauth2server.userauth.Role;
import com.mygordienko.spring.security.examples.oauth2server.oauth.port.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        createUserIfAbsent("admin", "admin", Set.of(Role.values()));
        createUserIfAbsent("user", "user", Set.of(Role.ROLE_USER));
        createUserIfAbsent("hr", "hr", Set.of(Role.ROLE_HR));
        createUserIfAbsent("manager", "manager", Set.of(Role.ROLE_MANAGER));
    }

    private void createUserIfAbsent(String username, String rawPassword, Set<Role> roles) {
        if (userRepository.findByUsername(username).isEmpty()) {
            var user = new AppUser();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRoles(roles);
            userRepository.save(user);
        }
    }
}

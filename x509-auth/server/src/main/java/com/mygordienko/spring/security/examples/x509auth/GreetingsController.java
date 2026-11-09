package com.mygordienko.spring.security.examples.x509auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class GreetingsController {
    private static final String MESSAGE_TEMPLATE = "Hello %s";

    @GetMapping("/hello")
    public GreetingDto echo(@AuthenticationPrincipal UserDetails user) {
        return new GreetingDto(MESSAGE_TEMPLATE.formatted(user.getUsername()));
    }

    public static class GreetingDto {
        public GreetingDto(String message) {
            this.message = message;
        }
        private final String message;

        public String getMessage() {
            return message;
        }
    }
}

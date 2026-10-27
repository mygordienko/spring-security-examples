package com.mygordienko.spring.security.examples.jwtauth;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/secured")
public class MainController {
    @GetMapping("/data-user")
    @PreAuthorize("hasAuthority('user')")
    @SecurityRequirement(name = "bearerAuth")
    public DataResponse getSecuredUser(Authentication authentication) {
        return new DataResponse("Secured user data for %s".formatted(authentication.getPrincipal()));
    }

    @GetMapping("/data-admin")
    @PreAuthorize("hasAuthority('admin')")
    @SecurityRequirement(name = "bearerAuth")
    public DataResponse getSecuredAdmin(Authentication authentication) {
        return new DataResponse("Secured admin data for %s".formatted(authentication.getPrincipal()));
    }

    public record DataResponse(String data){}
}

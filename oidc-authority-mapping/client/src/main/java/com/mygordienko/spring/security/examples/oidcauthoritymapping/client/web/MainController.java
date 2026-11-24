package com.mygordienko.spring.security.examples.oidcauthoritymapping.client.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        addOidcUserAttributes(authentication, model);
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/user")
    public String userPage(Authentication authentication, Model model) {
        addOidcUserAttributes(authentication, model);
        return "user";
    }

    @GetMapping("/restricted")
    public String restrictedPage(Authentication authentication, Model model) {
        addOidcUserAttributes(authentication, model);
        return "restricted";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

    private static void addOidcUserAttributes(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof OidcUser oidcUser) {
            model.addAttribute("subject", oidcUser.getSubject());
            model.addAttribute("authorities", oidcUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList());
        }
    }
}

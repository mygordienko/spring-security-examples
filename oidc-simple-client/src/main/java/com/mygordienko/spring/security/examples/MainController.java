package com.mygordienko.spring.security.examples;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String welcome(Model model, @AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        model.addAttribute("username", principal.getName());
        return "index";
    }
}

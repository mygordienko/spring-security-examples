package com.mygordienko.spring.security.examples.oauth2client.home.adapter;

import com.mygordienko.spring.security.examples.oauth2client.common.ui.LayoutModelFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final LayoutModelFactory layoutModelFactory;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OidcUser user, Model model) {
        model.addAllAttributes(layoutModelFactory.create(user, "home"));
        return "home";
    }
}

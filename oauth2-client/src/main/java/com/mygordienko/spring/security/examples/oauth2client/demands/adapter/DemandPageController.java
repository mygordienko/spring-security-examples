package com.mygordienko.spring.security.examples.oauth2client.demands.adapter;

import com.mygordienko.spring.security.examples.oauth2client.common.ui.LayoutModelFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DemandPageController {

    private final LayoutModelFactory layoutModelFactory;

    @GetMapping("/demands")
    public String demands(@AuthenticationPrincipal OidcUser user, Model model) {
        model.addAllAttributes(layoutModelFactory.create(user, "demands"));
        return "demands";
    }
}

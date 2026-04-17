package com.mygordienko.spring.security.examples.oauth2client.interviews.adapter;

import com.mygordienko.spring.security.examples.oauth2client.common.ui.LayoutModelFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class InterviewPageController {

    private final LayoutModelFactory layoutModelFactory;

    @GetMapping("/interviews")
    public String interviews(@AuthenticationPrincipal OidcUser user, Model model) {
        model.addAllAttributes(layoutModelFactory.create(user, "interviews"));
        return "interviews";
    }
}

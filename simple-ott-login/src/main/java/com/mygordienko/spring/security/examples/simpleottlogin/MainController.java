package com.mygordienko.spring.security.examples.simpleottlogin;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
  @GetMapping("/")
  public String welcome(Model model, @AuthenticationPrincipal UserDetails principal) {
    model.addAttribute("username", principal.getUsername());
    return "index";
  }

  @GetMapping("/ott/sent")
  public String welcome(Model model) {
    return "ott_sent";
  }
}

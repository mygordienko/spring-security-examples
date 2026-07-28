package com.mygordienko.spring.security.examples.mfaspecificendpoint;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {
  @GetMapping("/")
  public String welcome(Model model, @AuthenticationPrincipal UserDetails principal) {
    model.addAttribute("username", principal.getUsername());
    return "index";
  }

  @GetMapping("/adminpanel")
  public String adminpanel(Model model, @AuthenticationPrincipal UserDetails principal) {
    model.addAttribute("username", principal.getUsername());
    model.addAttribute("actionExecuted", false);
    return "adminpanel";
  }

  @PostMapping("/adminpanel")
  public String adminpanelAction(Model model, @AuthenticationPrincipal UserDetails principal) {
    model.addAttribute("username", principal.getUsername());
    model.addAttribute("actionExecuted", true);
    return "adminpanel";
  }

  @GetMapping("/ott/sent")
  public String welcome(Model model) {
    return "ott_sent";
  }
}

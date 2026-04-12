package com.mygordienko.spring.security.example.simpleldap;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
  @GetMapping("/")
  public String welcome(Model model, @AuthenticationPrincipal LdapUserDetailsImpl principal) {
    model.addAttribute("username", principal.getUsername());
    return "index";
  }
}

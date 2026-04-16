package com.mygordienko.spring.security.examples.oauth2server.oauth.adapter;

import com.mygordienko.spring.security.examples.oauth2server.oauth.ClientManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientManagementController {

    private final ClientManagementService clientManagementService;

    @GetMapping
    public String listClients(Model model) {
        model.addAttribute("clients", clientManagementService.findAll());
        return "clients/list";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("request", new RegisterClientRequest());
        return "clients/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterClientRequest request) {
        clientManagementService.registerClient(request);
        return "redirect:/clients";
    }

    @PostMapping("/delete/{clientId}")
    public String delete(@PathVariable String clientId) {
        clientManagementService.deleteClient(clientId);
        return "redirect:/clients";
    }
}
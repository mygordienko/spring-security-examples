package com.mygordienko.spring.security.examples.jwtauth;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {
  public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @PostMapping("/login")
  public AuthResponse authenticate(@RequestBody AuthRequest request) {
    var credentials = new UsernamePasswordAuthenticationToken(request.email(), request.password());

    // NOTE: throws exception in case of bad credentials
    authenticationManager.authenticate(credentials);

    var jwtToken = jwtService.generateToken(request.email());

    return new AuthResponse(jwtToken);
  }

  public record AuthRequest(String email, String password) {
  }

  public record AuthResponse(String token) {
  }
}

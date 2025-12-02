package com.petadoption.controller;

import com.petadoption.dto.LoginRequest;
import com.petadoption.dto.LoginResponse;
import com.petadoption.dto.SignupRequest;
import com.petadoption.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public LoginResponse signup(@RequestBody SignupRequest req) {
        return authService.signup(req);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return authService.login(req);
    }
}

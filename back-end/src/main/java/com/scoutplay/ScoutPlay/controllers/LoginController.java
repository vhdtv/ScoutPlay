package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.dto.LoginRequest;
import com.scoutplay.ScoutPlay.api.dto.LoginResponse;
import com.scoutplay.ScoutPlay.api.dto.UserSummary;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.security.SecurityUtils;
import com.scoutplay.ScoutPlay.services.LoginService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.Cookie;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserSummary>> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = loginService.autenticarUsuario(request);
        Cookie cookie = new Cookie("access_token", loginResponse.getTokenAcesso());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(Math.toIntExact(loginResponse.getExpiraEm()));
        response.addCookie(cookie);
        return ResponseEntity.ok(ApiResponse.success(loginResponse.getUsuario(), "Login realizado com sucesso"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserSummary>> me() {
        UUID aliasId = UUID.fromString(SecurityUtils.currentUserId());
        UserSummary response = loginService.buscarUsuarioPorAliasId(aliasId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

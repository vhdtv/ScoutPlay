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

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", loginResponse.getTokenAcesso())
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(loginResponse.getExpiraEm())
            .sameSite("Lax")
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .body(ApiResponse.success(loginResponse.getUsuario(), "Login realizado com sucesso"));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<UserSummary>> getUserData(@CookieValue(name = "access_token", required = false) String accessToken) {
        UUID aliasId = UUID.fromString(SecurityUtils.currentUserId());
        UserSummary response = loginService.buscarUsuarioPorAliasId(aliasId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

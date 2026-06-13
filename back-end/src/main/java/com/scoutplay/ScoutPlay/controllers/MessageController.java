package com.scoutplay.ScoutPlay.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutplay.ScoutPlay.api.dto.ConviteInputDTO;
import com.scoutplay.ScoutPlay.api.dto.ConviteOutputDTO;
import com.scoutplay.ScoutPlay.api.dto.UserSummaryDTO;
import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.models.Convite;
import com.scoutplay.ScoutPlay.security.SecurityUtils;
import com.scoutplay.ScoutPlay.services.ConviteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {
    final ConviteService conviteService;

    @GetMapping("/message")
    public ResponseEntity<ApiResponse<List<ConviteOutputDTO>>> obter() {
        UUID usuarioLogado = UUID.fromString(SecurityUtils.currentUserId());
        List<ConviteOutputDTO> result = conviteService.findAllByDestinatario(usuarioLogado);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/message")
    public ResponseEntity<ApiResponse<ConviteOutputDTO>> enviar(@RequestBody ConviteInputDTO dto) {
        UUID usuarioLogado = UUID.fromString(SecurityUtils.currentUserId());
        try {
            ConviteOutputDTO result = conviteService.criar(usuarioLogado, dto.getUsernameDestinatario(), dto.getMensagem());
            return ResponseEntity.ok(ApiResponse.success(result));
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("404", ""));
        }
    }
    
    // @PostMapping("/message/{invite_id}/accept")
    // public ResponseEntity<ApiResponse<ConviteInputDTO>> aceitar(@PathVariable String username, ConviteInputDTO dto) {}
    
    // @PostMapping("/message/{invite_id}/decline")
    // public ResponseEntity<ApiResponse<ConviteInputDTO>> rejeitar(@PathVariable String username) {}
}
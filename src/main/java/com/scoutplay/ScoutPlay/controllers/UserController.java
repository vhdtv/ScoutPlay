package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.api.response.ApiResponse;
import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.security.SecurityUtils;
import com.scoutplay.ScoutPlay.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Usuario>> updateUser(@PathVariable String id, @RequestBody Usuario usuarioAtualizado) {
        if (!SecurityUtils.isOwner(id)) {
            throw new AccessDeniedException("Você não tem permissão para alterar este usuário.");
        }

        return userService.findById(id).map(usuario -> {
            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setTelefone(usuarioAtualizado.getTelefone());
            usuario.setCep(usuarioAtualizado.getCep());
            Usuario updated = userService.save(usuario);
            return ResponseEntity.ok(ApiResponse.success(updated, "Usuário atualizado com sucesso"));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        if (!SecurityUtils.isOwner(id)) {
            throw new AccessDeniedException("Você não tem permissão para remover este usuário.");
        }

        return userService.findById(id).map(usuario -> {
            userService.delete(usuario);
            return ResponseEntity.ok(ApiResponse.<Void>success(null, "Usuário removido com sucesso"));
        }).orElse(ResponseEntity.notFound().build());
    }
}

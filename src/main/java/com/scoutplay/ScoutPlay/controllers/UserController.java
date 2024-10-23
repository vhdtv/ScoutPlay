package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    // Buscar todos os usuários
    @GetMapping
    public List<Usuario> getAllUsers() {
        return userService.findAll();
    }


    // Atualizar um usuário existente
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUser(@PathVariable String id, @RequestBody Usuario usuarioAtualizado) {
        Optional<Usuario> usuario = userService.findById(id);
        if (usuario.isPresent()) {
            Usuario usuarioParaAtualizar = usuario.get();
            usuarioParaAtualizar.setNome(usuarioAtualizado.getNome());
            usuarioParaAtualizar.setTelefone(usuarioAtualizado.getTelefone());
            usuarioParaAtualizar.setCpf(usuarioAtualizado.getCpf());
            usuarioParaAtualizar.setDataNascimento(usuarioAtualizado.getDataNascimento());
            usuarioParaAtualizar.setCep(usuarioAtualizado.getCep());
            usuarioParaAtualizar.setEmail(usuarioAtualizado.getEmail());
            usuarioParaAtualizar.setSenha(usuarioAtualizado.getSenha());

            Usuario updatedUsuario = userService.save(usuarioParaAtualizar);
            return ResponseEntity.ok(updatedUsuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Deletar um usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        Optional<Usuario> usuario = userService.findById(id);
        if (usuario.isPresent()) {
            userService.delete(usuario.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
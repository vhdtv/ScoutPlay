package com.scoutplay.ScoutPlay.controllers;

import com.scoutplay.ScoutPlay.models.Usuario;
import com.scoutplay.ScoutPlay.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/api/login")
    public String login(@RequestParam("email") String email, @RequestParam("senha") String senha, Model model){
        Optional<Usuario> usuario = loginService.autenticar(email, senha);
        if (usuario.isPresent()){
            if (usuario.get() instanceof com.scoutplay.ScoutPlay.models.Atleta){
                return "redirect:/"; //Redirecionar para o painel  do atleta

            } else if (usuario.get() instanceof com.scoutplay.ScoutPlay.models.Olheiro) {
                return "redirect:/"; //Redirecionar para o painel  do olheiro

            }
        }
        model.addAttribute("erro", "Usuário ou senha inválidos");
        return "login";
    }

}

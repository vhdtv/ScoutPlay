package com.scoutplay.ScoutPlay.services;

import java.time.LocalDate;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.scoutplay.ScoutPlay.models.Usuario;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    private HashMap<String, Usuario> usuarios;
    @BeforeEach
    void setUp() {
        this.usuarios.put("Fabiano Moreira", new Usuario("Fabiano", "Moreira", "fabiano@atleta.com", "123.456.789-00", "senha123", LocalDate.of(2004, 12, 04)));
        this.usuarios.put("Clarice Moreira", new Usuario("Clarice", "Moreira", "clarice@atleta.com", "123.456.789-01", "senha123", LocalDate.of(2000, 07, 12)));
    }

}
package com.scoutplay.ScoutPlay.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String remetente;

    public void enviarLinkRecuperacao(String destinatario, String link) {
        if (remetente == null || remetente.isBlank()) {
            throw new IllegalStateException("Servidor de e-mail não configurado");
        }

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(remetente);
        msg.setTo(destinatario);
        msg.setSubject("ScoutPlay - Redefinição de senha");
        msg.setText(
            "Olá!\n\n" +
            "Recebemos uma solicitação de redefinição de senha para sua conta ScoutPlay.\n\n" +
            "Use o link abaixo. Ele é temporário e só pode ser utilizado uma vez:\n\n" +
            link + "\n\n" +
            "Se você não solicitou essa alteração, ignore este e-mail.\n\n" +
            "— Equipe ScoutPlay"
        );
        mailSender.send(msg);
        log.info("E-mail de recuperação enviado");
    }
}

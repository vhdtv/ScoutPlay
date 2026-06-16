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

    @Value("${spring.mail.username}")
    private String remetente;

    public void enviarNovaSenha(String destinatario, String novaSenha) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(remetente);
            msg.setTo(destinatario);
            msg.setSubject("ScoutPlay - Nova senha temporaria");
            msg.setText(
                "Olá!\n\n" +
                "Recebemos uma solicitação de recuperação de senha para a sua conta no ScoutPlay.\n\n" +
                "Sua nova senha temporária é:\n\n" +
                "    " + novaSenha + "\n\n" +
                "Acesse a plataforma e troque para uma senha de sua preferência nas configurações.\n\n" +
                "Se você não solicitou essa alteração, ignore este e-mail.\n\n" +
                "— Equipe ScoutPlay"
            );
            mailSender.send(msg);
            log.info("E-mail de recuperação enviado para {}", destinatario);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail para {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail de recuperação", e);
        }
    }
}

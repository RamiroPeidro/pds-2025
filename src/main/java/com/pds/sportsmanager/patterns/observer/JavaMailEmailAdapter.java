package com.pds.sportsmanager.patterns.observer;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component("emailAdapterJavaMail")
@RequiredArgsConstructor
public class JavaMailEmailAdapter implements EmailAdapter {

    private final JavaMailSender mailSender;

    @Override
    public void enviarNotificacion(String destinatario, String asunto, String cuerpoHtml) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpoHtml, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando email", e);
        }
    }

}

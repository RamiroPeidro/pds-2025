package com.pds.sportsmanager.patterns.observer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("emailAdapterMock")
@Profile("test")
@Slf4j
public class MockEmailAdapter implements EmailAdapter {

    @Override
    public void enviarNotificacion(String destinatario, String asunto, String cuerpoHtml) {
        log.info("[MOCK EMAIL] Para: {} | Asunto: {} | Cuerpo: {}", destinatario, asunto, cuerpoHtml);
    }
}
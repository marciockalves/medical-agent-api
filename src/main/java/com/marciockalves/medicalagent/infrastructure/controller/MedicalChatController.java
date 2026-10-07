package com.marciockalves.medicalagent.infrastructure.controller;

import com.marciockalves.medicalagent.infrastructure.ai.AppointmentTools;
import com.marciockalves.medicalagent.infrastructure.ai.PatientTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j // Para usar log.info()
public class MedicalChatController {

    private final ChatClient chatClient;
    private final PatientTools patientTools;
    private final AppointmentTools appointmentTools;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(
            @RequestParam String sessionId,
            @RequestParam String message) {

        // 1. Loga a mensagem de entrada do usuário imediatamente
        log.info("[SESSÃO: {}] Entrada do Usuário: '{}'", sessionId, message);

        return this.chatClient.prompt()
                .user(message)
                .tools(patientTools, appointmentTools)
                .advisors(advisor -> advisor.param("chat_memory_conversation_id", sessionId))
                .stream()
                .content()
                // 2. Imprime cada pedaço (chunk) que a IA gera no console em tempo real
                .doOnNext(chunk -> System.out.print(chunk))
                // 3. Quando o stream termina, quebra uma linha no log para organizar
                .doOnComplete(() -> log.info("\n[SESSÃO: {}] Resposta finalizada com sucesso.\n", sessionId));
    }
}
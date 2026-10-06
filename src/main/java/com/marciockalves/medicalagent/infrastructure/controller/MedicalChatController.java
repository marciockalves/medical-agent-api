package com.marciockalves.medicalagent.infrastructure.controller;

import com.marciockalves.medicalagent.infrastructure.ai.PatientTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class MedicalChatController {

    private final ChatClient chatClient;
    private final PatientTools patientTools; // Injeção do Adapter da Tool de IA


    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(
            @RequestParam String sessionId,
            @RequestParam String message) {

        return this.chatClient.prompt()
                .user(message)

                // Registra o PatientTools no ChatClient do Spring AI.
                // O Spring AI envia essa especificação para a OpenAI.
                // Se a IA decidir acionar a busca de paciente, o Spring AI chama
                // o PatientTools -> FindPatientByNameUseCase -> Banco e devolve o resultado.
                .tools(patientTools)

                .advisors(advisor -> advisor.param("chat_memory_conversation_id", sessionId))
                .stream()
                .content();
    }
}
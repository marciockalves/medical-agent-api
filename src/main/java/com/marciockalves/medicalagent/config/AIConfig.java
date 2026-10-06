package com.marciockalves.medicalagent.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class AIConfig {

    @Value("classpath:prompts/system-prompt.st")
    private Resource systemPromptResource;

    @Bean
    public ChatMemory chatMemory() {
        // No Spring AI 2.0+, usa-se o Builder para configurar a janela de mensagens
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        // No Spring AI 2.0+, o MessageChatMemoryAdvisor também é instanciado via Builder
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .build();

        return builder
                .defaultSystem(systemPromptResource)
                .defaultAdvisors(memoryAdvisor)
                .build();
    }
}
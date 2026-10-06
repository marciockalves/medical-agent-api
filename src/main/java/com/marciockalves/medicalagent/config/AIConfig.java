package com.marciockalves.medicalagent.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class AIConfig {
    @Value("classpath:prompts/system-prompt.st")
    private Resource systemPromptResource;
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {

        return builder
                .defaultSystem(systemPromptResource)
                .build();
    }
}
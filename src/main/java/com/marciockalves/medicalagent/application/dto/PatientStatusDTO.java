package com.marciockalves.medicalagent.application.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PatientStatusDTO(
        boolean found,
        UUID patientId,
        String fullName,
        boolean needsUpdate,
        String message
) {
    public static PatientStatusDTO notFound(String name) {
        return PatientStatusDTO.builder()
                .found(false)
                .message("Nenhum paciente encontrado com o nome: " + name)
                .build();
    }
}
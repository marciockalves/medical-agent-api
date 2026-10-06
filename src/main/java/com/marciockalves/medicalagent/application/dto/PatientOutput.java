package com.marciockalves.medicalagent.application.dto;
import com.marciockalves.medicalagent.domain.entity.Patient;
import java.time.LocalDate;
import java.util.UUID;

public record PatientOutput(
        UUID id,
        String fullName,
        String cpf,
        LocalDate birthDate,
        String email,
        String phone
) {
    public static PatientOutput fromEntity(Patient patient) {
        return new PatientOutput(
                patient.getId(),
                patient.getFullName(),
                patient.getCpf(),
                patient.getBirthDate(),
                patient.getEmail(),
                patient.getPhone()
        );
    }
}
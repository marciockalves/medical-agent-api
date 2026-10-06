package com.marciockalves.medicalagent.application.dto;
import java.time.LocalDate;

public record CreatePatientInput(
        String fullName,
        String cpf,
        LocalDate birthDate,
        String email,
        String phone
) {}
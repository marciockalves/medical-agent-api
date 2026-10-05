package com.marciockalves.medicalagent.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAppointmentRequest(
        String patientCpf,
        String patientFullName,
        String patientPhone,
        UUID doctorId,
        LocalDateTime appointmentDateTime
) {}
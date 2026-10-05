package com.marciockalves.medicalagent.domain.enums;

public enum AppointmentStatus {
    SCHEDULED,   // Agendado (aguardando realização)
    CONFIRMED,   // Confirmado pelo paciente/clínica
    CANCELLED,   // Cancelado
    COMPLETED    // Consulta realizada
}
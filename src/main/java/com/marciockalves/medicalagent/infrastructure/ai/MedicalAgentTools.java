package com.marciockalves.medicalagent.infrastructure.ai;

import com.marciockalves.medicalagent.application.dto.AvailableSlotsResponse;
import com.marciockalves.medicalagent.application.dto.CreateAppointmentRequest;
import com.marciockalves.medicalagent.application.usecase.ManageAppointmentUseCase;
import lombok.RequiredArgsConstructor;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MedicalAgentTools {

    private final ManageAppointmentUseCase manageAppointmentUseCase;

    @Tool(description = "Busca os horários vagos de um médico para uma determinada semana a partir de uma data inicial (YYYY-MM-DD).")
    public AvailableSlotsResponse checkAvailableSlots(String doctorId, String startDate) {
        return manageAppointmentUseCase.checkAvailableSlots(
                UUID.fromString(doctorId),
                LocalDate.parse(startDate)
        );
    }

    @Tool(description = "Cria um agendamento de consulta médica no sistema para o paciente informado.")
    public String createAppointment(CreateAppointmentRequest request) {
        return manageAppointmentUseCase.createAppointment(request);
    }
}
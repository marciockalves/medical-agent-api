package com.marciockalves.medicalagent.infrastructure.ai;

import com.marciockalves.medicalagent.application.usecase.CalculateAvailableSlotsUseCase;
import com.marciockalves.medicalagent.application.usecase.ListDoctorsUseCase;
import com.marciockalves.medicalagent.domain.entity.Doctor;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AppointmentTools {

    private final ListDoctorsUseCase listDoctorsUseCase;
    private final CalculateAvailableSlotsUseCase calculateAvailableSlotsUseCase;

    @Tool(description = "List all available medical specialties and doctors in the clinic for appointment scheduling.")
    public String listAvailableDoctorsAndSpecialties() {
        try {
            List<Doctor> doctors = listDoctorsUseCase.execute();

            return doctors.stream()
                    .map(d -> String.format("- Dr(a). %s | Especialidade: %s | CRM: %s",
                            d.getFullName(), d.getSpecialty(), d.getCrm()))
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            return "Erro ao buscar médicos disponíveis: " + e.getMessage();
        }
    }
    @Tool(description = "Check available appointment slots for a specific doctor, their name, and period ('MORNING', 'AFTERNOON' or 'BOTH') within the next business days.")
    public String checkAvailableSlots(
            @ToolParam(description = "The UUID of the selected doctor") UUID doctorId,
            @ToolParam(description = "The full name of the doctor") String docName,
            @ToolParam(description = "Preferred period: 'MORNING', 'AFTERNOON' or 'BOTH'") String period
    ) {
        try {
            return calculateAvailableSlotsUseCase.execute(doctorId, docName, period, 5);
        } catch (Exception e) {
            return "Erro ao consultar horários livres: " + e.getMessage();
        }
    }
}
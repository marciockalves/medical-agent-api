package com.marciockalves.medicalagent.infrastructure.ai;

import com.marciockalves.medicalagent.application.dto.PatientStatusDTO;
import com.marciockalves.medicalagent.application.usecase.FindPatientByNameUseCase;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PatientTools {

    private final FindPatientByNameUseCase findPatientByNameUseCase;

    @Tool(description = "Busca o cadastro de um paciente pelo nome completo ou parcial para verificar a situação do cadastro.")
    public String searchPatientByName(String fullName) {
        PatientStatusDTO status = findPatientByNameUseCase.execute(fullName);
        return status.toString();
    }
}
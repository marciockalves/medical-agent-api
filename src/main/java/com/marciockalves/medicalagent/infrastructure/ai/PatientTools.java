package com.marciockalves.medicalagent.infrastructure.ai;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.marciockalves.medicalagent.application.dto.CreatePatientInput;
import com.marciockalves.medicalagent.application.dto.PatientOutput;
import com.marciockalves.medicalagent.application.dto.PatientStatusDTO;
import com.marciockalves.medicalagent.application.usecase.CreatePatientUseCase;
import com.marciockalves.medicalagent.application.usecase.FindPatientByNameUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class PatientTools {

    private final FindPatientByNameUseCase findPatientByNameUseCase;
    private final CreatePatientUseCase createPatientUseCase;

    @Tool(description = "Search for a patient's registration status by full name. Do NOT call if the user is confirming a registration.")
    public String searchPatientByName(String fullName) {
        // Se a ferramenta for chamada com palavras de confirmação em vez de um nome
        if (fullName == null || fullName.trim().split("\\s+").length < 1) {
            return "Nenhum nome válido fornecido.";
        }

        PatientStatusDTO status = findPatientByNameUseCase.execute(fullName);
        return status.toString();
    }

    public record CreatePatientRequest(
            @JsonPropertyDescription("Patient full name") String fullName,
            @JsonPropertyDescription("Patient CPF with 11 digits") String cpf,
            @JsonPropertyDescription("Date of birth in YYYY-MM-DD format") String birthDate,
            @JsonPropertyDescription("Patient email address") String email,
            @JsonPropertyDescription("Patient phone number with area code") String phone
    ) {}

    @Tool(description = "Registers a new patient into the system after collecting CPF, birth date, email and phone.")
    public String createPatient(CreatePatientRequest request) {
        try {
            LocalDate parsedBirthDate = LocalDate.parse(request.birthDate());

            CreatePatientInput input = new CreatePatientInput(
                    request.fullName(),
                    request.cpf(),
                    parsedBirthDate,
                    request.email(),
                    request.phone()
            );

            PatientOutput output = createPatientUseCase.execute(input);
            return String.format("Paciente %s cadastrado com sucesso! ID: %s", output.fullName(), output.id());
        } catch (Exception e) {
            return "Erro ao realizar o cadastro: " + e.getMessage();
        }
    }
}
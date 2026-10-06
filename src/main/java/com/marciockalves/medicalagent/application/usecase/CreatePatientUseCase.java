package com.marciockalves.medicalagent.application.usecase;


import com.marciockalves.medicalagent.application.dto.CreatePatientInput;
import com.marciockalves.medicalagent.application.dto.PatientOutput;
import com.marciockalves.medicalagent.domain.entity.Patient;
import com.marciockalves.medicalagent.domain.port.PatientRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatePatientUseCase {

    private final PatientRepositoryPort patientRepositoryPort; // Injeção via Porta Hexagonal

    @Transactional
    public PatientOutput execute(CreatePatientInput input) {
        String cleanCpf = input.cpf().replaceAll("\\D", "");

        if (patientRepositoryPort.existsByCpf(cleanCpf)) {
            throw new IllegalArgumentException("Já existe um paciente cadastrado com este CPF.");
        }
        if (patientRepositoryPort.existsByEmail(input.email())) {
            throw new IllegalArgumentException("Já existe um paciente cadastrado com este e-mail.");
        }

        Patient patient = Patient.builder()
                .fullName(input.fullName())
                .cpf(cleanCpf)
                .birthDate(input.birthDate())
                .email(input.email())
                .phone(input.phone())
                .build();

        Patient saved = patientRepositoryPort.save(patient);
        return PatientOutput.fromEntity(saved);
    }
}
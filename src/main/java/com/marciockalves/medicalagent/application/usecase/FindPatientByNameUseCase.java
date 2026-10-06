package com.marciockalves.medicalagent.application.usecase;

import com.marciockalves.medicalagent.application.dto.PatientStatusDTO;
import com.marciockalves.medicalagent.domain.entity.Patient;
import com.marciockalves.medicalagent.domain.port.PatientRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FindPatientByNameUseCase {

    private final PatientRepositoryPort patientRepository;

    public PatientStatusDTO execute(String fullName) {
        List<Patient> patients = patientRepository.findByFullNameContainingIgnoreCase(fullName);

        if (patients.isEmpty()) {
            return PatientStatusDTO.notFound(fullName);
        }

        Patient patient = patients.get(0);

        // Regra dos 6 meses: se nunca atualizou ou atualizou há mais de 6 meses
        boolean needsUpdate = patient.getUpdatedAt() == null ||
                patient.getUpdatedAt().isBefore(LocalDate.now().minusMonths(6).atStartOfDay());

        return PatientStatusDTO.builder()
                .found(true)
                .patientId(patient.getId())
                .fullName(patient.getFullName())
                .needsUpdate(needsUpdate)
                .message(needsUpdate
                        ? "Paciente encontrado. O cadastro necessita de atualização (última atualização há mais de 6 meses)."
                        : "Paciente encontrado com cadastro atualizado.")
                .build();
    }

}
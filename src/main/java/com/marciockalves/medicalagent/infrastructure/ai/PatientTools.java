package com.marciockalves.medicalagent.infrastructure.ai;

import com.marciockalves.medicalagent.domain.entity.Patient;
import com.marciockalves.medicalagent.domain.port.PatientRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PatientTools {

    private final PatientRepositoryPort patientRepository;

    @Tool(description = "Busca um paciente cadastrado pelo nome completo ou parcial. Retorna os dados do paciente e se o cadastro precisa de atualização (> 6 meses).")
    public String searchPatientByName(String fullName) {
        List<Patient> patients = patientRepository.findByFullNameContainingIgnoreCase(fullName);

        if (patients.isEmpty()) {
            return "Nenhum paciente encontrado com o nome '" + fullName + "'.";
        }

        if (patients.size() > 1) {
            return "Foram encontrados " + patients.size() + " pacientes com esse nome. Peça o CPF para confirmar a identidade com segurança.";
        }

        Patient patient = patients.get(0);
        boolean needsUpdate = patient.getUpdatedAt().isBefore(LocalDateTime.now().minusMonths(6));

        return String.format(
                "Paciente encontrado: ID=%s, Nome=%s, Telefone=%s, CEP=%s, NecessitaAtualizacaoCadastro=%b",
                patient.getId(),
                patient.getFullName(),
                patient.getPhone(),
                patient.getZipCode(),
                needsUpdate
        );
    }
}
package com.marciockalves.medicalagent.infrastructure.persistence;

import com.marciockalves.medicalagent.domain.entity.Patient;
import com.marciockalves.medicalagent.domain.port.PatientRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PatientRepositoryAdapter implements PatientRepositoryPort {

    private final PatientRepository repository;

    @Override
    public Patient save(Patient patient) {
        return repository.save(patient);
    }

    @Override
    public Optional<Patient> findByCpf(String cpf) {
        return repository.findByCpf(cpf);
    }

    @Override
    public Optional<Patient> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return repository.existsByCpf(cpf);
    }
}
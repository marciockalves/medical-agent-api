package com.marciockalves.medicalagent.domain.port;

import com.marciockalves.medicalagent.domain.entity.Patient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepositoryPort {
    Patient save(Patient patient);
    Optional<Patient> findByCpf(String cpf);
    Optional<Patient> findById(UUID id);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email); // <-- Novo método adicionado
    List<Patient> findByFullNameContainingIgnoreCase(String name);
}
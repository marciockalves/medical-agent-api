package com.marciockalves.medicalagent.infrastructure.persistence;

import com.marciockalves.medicalagent.domain.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email); // <-- Novo método adicionado

    List<Patient> findByFullNameContainingIgnoreCase(String name);
}
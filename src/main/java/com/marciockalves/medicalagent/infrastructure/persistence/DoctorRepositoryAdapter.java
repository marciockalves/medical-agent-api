package com.marciockalves.medicalagent.infrastructure.persistence;

import com.marciockalves.medicalagent.domain.entity.Doctor;
import com.marciockalves.medicalagent.domain.port.DoctorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DoctorRepositoryAdapter implements DoctorRepositoryPort {

    private final DoctorRepository repository;

    @Override
    public List<Doctor> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Doctor> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Doctor> findBySpecialtyIgnoreCase(String specialty) {
        return repository.findBySpecialtyIgnoreCase(specialty);
    }
}
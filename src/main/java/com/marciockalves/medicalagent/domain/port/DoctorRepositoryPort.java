package com.marciockalves.medicalagent.domain.port;

import com.marciockalves.medicalagent.domain.entity.Doctor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorRepositoryPort {
    List<Doctor> findAll();
    Optional<Doctor> findById(UUID id);
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}
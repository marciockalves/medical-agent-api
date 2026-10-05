package com.marciockalves.medicalagent.infrastructure.persistence;

import com.marciockalves.medicalagent.domain.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    Optional<Doctor> findByCrm(String crm);

    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}
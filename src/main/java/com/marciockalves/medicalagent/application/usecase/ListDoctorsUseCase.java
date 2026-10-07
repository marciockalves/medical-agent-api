package com.marciockalves.medicalagent.application.usecase;

import com.marciockalves.medicalagent.domain.entity.Doctor;
import com.marciockalves.medicalagent.domain.port.DoctorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListDoctorsUseCase {

    private final DoctorRepositoryPort doctorRepositoryPort;

    public List<Doctor> execute() {
        List<Doctor> doctors = doctorRepositoryPort.findAll();
        if (doctors.isEmpty()) {
            throw new IllegalStateException("Nenhum médico ou especialidade cadastrada no sistema.");
        }
        return doctors;
    }
}
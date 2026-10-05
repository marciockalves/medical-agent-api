package com.marciockalves.medicalagent.infrastructure.persistence;

import com.marciockalves.medicalagent.domain.entity.Appointment;
import com.marciockalves.medicalagent.domain.enums.AppointmentStatus;
import com.marciockalves.medicalagent.domain.port.AppointmentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AppointmentRepositoryAdapter implements AppointmentRepositoryPort {

    private final AppointmentRepository repository;

    @Override
    public Appointment save(Appointment appointment) {
        return repository.save(appointment);
    }

    @Override
    public List<Appointment> findActiveByPatientCpf(String cpf) {
        return repository.findByPatientCpfAndStatusInOrderByAppointmentDateTimeAsc(
                cpf,
                List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)
        );
    }

    @Override
    public List<Appointment> findOccupiedSlots(UUID doctorId, LocalDateTime start, LocalDateTime end) {
        return repository.findOccupiedSlots(
                doctorId,
                List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED),
                start,
                end
        );
    }
}
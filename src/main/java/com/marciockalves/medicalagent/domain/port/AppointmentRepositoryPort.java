package com.marciockalves.medicalagent.domain.port;

import com.marciockalves.medicalagent.domain.entity.Appointment;
import com.marciockalves.medicalagent.domain.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepositoryPort {
    Appointment save(Appointment appointment);
    List<Appointment> findActiveByPatientCpf(String cpf);
    List<Appointment> findOccupiedSlots(UUID doctorId, LocalDateTime start, LocalDateTime end);
}
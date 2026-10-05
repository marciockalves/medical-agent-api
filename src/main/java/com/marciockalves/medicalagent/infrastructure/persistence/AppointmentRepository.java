package com.marciockalves.medicalagent.infrastructure.persistence;

import com.marciockalves.medicalagent.domain.entity.Appointment;
import com.marciockalves.medicalagent.domain.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    // Busca consultas ativas de um paciente específico (para listagem no chat)
    List<Appointment> findByPatientCpfAndStatusInOrderByAppointmentDateTimeAsc(
            String cpf,
            List<AppointmentStatus> statuses
    );

    // Consulta os horários já ocupados de um médico num determinado intervalo de datas
    @Query("""
        SELECT a FROM Appointment a 
        WHERE a.doctor.id = :doctorId 
          AND a.status IN :statuses 
          AND a.appointmentDateTime BETWEEN :startDateTime AND :endDateTime
        ORDER BY a.appointmentDateTime ASC
    """)
    List<Appointment> findOccupiedSlots(
            @Param("doctorId") UUID doctorId,
            @Param("statuses") List<AppointmentStatus> statuses,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}
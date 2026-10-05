package com.marciockalves.medicalagent.application.usecase;

import com.marciockalves.medicalagent.application.dto.AvailableSlotsResponse;
import com.marciockalves.medicalagent.application.dto.CreateAppointmentRequest;
import com.marciockalves.medicalagent.domain.entity.Appointment;
import com.marciockalves.medicalagent.domain.entity.Doctor;
import com.marciockalves.medicalagent.domain.entity.Patient;
import com.marciockalves.medicalagent.domain.enums.AppointmentStatus;
import com.marciockalves.medicalagent.domain.port.AppointmentRepositoryPort;
import com.marciockalves.medicalagent.domain.port.PatientRepositoryPort;
import com.marciockalves.medicalagent.infrastructure.persistence.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManageAppointmentUseCase {

    private final AppointmentRepositoryPort appointmentRepository;
    private final PatientRepositoryPort patientRepository;
    private final DoctorRepository doctorRepository;

    @Transactional(readOnly = true)
    public AvailableSlotsResponse checkAvailableSlots(UUID doctorId, LocalDate startDate) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado."));

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = startDate.plusDays(7).atTime(LocalTime.MAX);

        List<Appointment> occupied = appointmentRepository.findOccupiedSlots(doctorId, startDateTime, endDateTime);
        List<LocalDateTime> occupiedTimes = occupied.stream().map(Appointment::getAppointmentDateTime).toList();

        List<String> morning = new ArrayList<>();
        List<String> afternoon = new ArrayList<>();

        // Algoritmo simples de janelas de atendimento (8h-12h e 14h-18h)
        for (int day = 0; day < 7; day++) {
            LocalDate currentDay = startDate.plusDays(day);

            // Manhã
            for (int hour = 8; hour < 12; hour++) {
                LocalDateTime slot = currentDay.atTime(hour, 0);
                if (!occupiedTimes.contains(slot)) {
                    morning.add(slot.toString());
                }
            }
            // Tarde
            for (int hour = 14; hour < 18; hour++) {
                LocalDateTime slot = currentDay.atTime(hour, 0);
                if (!occupiedTimes.contains(slot)) {
                    afternoon.add(slot.toString());
                }
            }
        }

        return new AvailableSlotsResponse(
                doctor.getFullName(),
                "Disponibilidade para a semana de " + startDate,
                morning,
                afternoon
        );
    }

    @Transactional
    public String createAppointment(CreateAppointmentRequest request) {
        Patient patient = patientRepository.findByCpf(request.patientCpf())
                .orElseGet(() -> patientRepository.save(
                        Patient.builder()
                                .cpf(request.patientCpf())
                                .fullName(request.patientFullName())
                                .phone(request.patientPhone())
                                .birthDate(LocalDate.of(1990, 1, 1)) // Default fallback
                                .zipCode("00000000")
                                .street("Não informado")
                                .number("S/N")
                                .neighborhood("Centro")
                                .city("Balneário Camboriú")
                                .state("SC")
                                .build()
                ));

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado"));

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDateTime(request.appointmentDateTime())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        appointmentRepository.save(appointment);
        return "Consulta agendada com sucesso para " + request.appointmentDateTime() + " com " + doctor.getFullName();
    }
}
package com.marciockalves.medicalagent.application.usecase;

import com.marciockalves.medicalagent.domain.entity.Appointment;
import com.marciockalves.medicalagent.domain.port.AppointmentRepositoryPort;
import com.marciockalves.medicalagent.domain.valueobject.TimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalculateAvailableSlotsUseCase {

    private final AppointmentRepositoryPort appointmentRepositoryPort;

    public String execute(UUID doctorId, String docName, String period, int daysAhead) {
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = addBusinessDays(startDateTime, daysAhead);

        List<Appointment> occupiedAppointments = appointmentRepositoryPort.findOccupiedSlots(
                doctorId, startDateTime, endDateTime
        );

        if (occupiedAppointments == null) {
            occupiedAppointments = Collections.emptyList();
        }

        List<TimeRange> workRanges = getWorkRanges(period);
        List<String> availableSlots = calculateAvailableSlots(startDateTime, endDateTime, workRanges, occupiedAppointments);

        if (availableSlots.isEmpty()) {
            return "Não há horários disponíveis para o período selecionado nos próximos dias úteis.";
        }

        String rawSlots = String.join("\n", availableSlots);

        // Envolve a listagem em tags XML para forçar o LLM a tratá-la como dado literal intocável
        return String.format("Abaixo estão os horários oficiais disponíveis para o(a) Dr(a). %s. Exiba-os exatamente como estão dentro da tag, sem alterar nenhuma linha, data ou formato:\n<horarios_disponiveis>\n%s\n</horarios_disponiveis>",
                docName, rawSlots);
    }

    private List<TimeRange> getWorkRanges(String period) {
        if (period.equalsIgnoreCase("MORNING")) {
            return List.of(new TimeRange(LocalTime.of(8, 0), LocalTime.of(12, 0)));
        } else if (period.equalsIgnoreCase("AFTERNOON")) {
            return List.of(new TimeRange(LocalTime.of(13, 0), LocalTime.of(17, 0)));
        } else {
            return List.of(
                    new TimeRange(LocalTime.of(8, 0), LocalTime.of(12, 0)),
                    new TimeRange(LocalTime.of(13, 0), LocalTime.of(17, 0))
            );
        }
    }

    private List<String> calculateAvailableSlots(LocalDateTime start, LocalDateTime end, List<TimeRange> workRanges, List<Appointment> occupied) {
        List<String> formattedSlots = new ArrayList<>();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", new Locale("pt", "BR"));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate currentDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();
        int counter = 1;

        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {

                for (TimeRange workHours : workRanges) {
                    LocalTime slotTime = workHours.start();

                    while (slotTime.plusHours(1).isBefore(workHours.end()) || slotTime.plusHours(1).equals(workHours.end())) {
                        LocalDateTime candidate = LocalDateTime.of(currentDate, slotTime);
                        LocalDateTime slotEndCandidate = candidate.plusHours(1);

                        boolean isFuture = candidate.isAfter(LocalDateTime.now());
                        boolean isOccupied = occupied.stream()
                                .anyMatch(app -> app.getAppointmentDateTime().equals(candidate));

                        if (isFuture && !isOccupied) {
                            String dayOfWeek = currentDate.format(dayOfWeekFormatter);
                            String dateStr = currentDate.format(dateFormatter);
                            String startTimeStr = slotTime.format(timeFormatter);
                            String endTimeStr = slotEndCandidate.format(timeFormatter);

                            // Formato numerado imutável: 1. terça-feira 18/10/2026 das 08:00 às 09:00
                            String slotDescription = String.format("%d. %s %s das %s às %s", counter++, dayOfWeek, dateStr, startTimeStr, endTimeStr);
                            formattedSlots.add(slotDescription);
                        }

                        slotTime = slotTime.plusHours(1);
                    }
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        return formattedSlots;
    }

    private LocalDateTime addBusinessDays(LocalDateTime date, int businessDays) {
        LocalDateTime result = date;
        int addedDays = 0;
        while (addedDays < businessDays) {
            result = result.plusDays(1);
            if (result.getDayOfWeek() != DayOfWeek.SATURDAY && result.getDayOfWeek() != DayOfWeek.SUNDAY) {
                addedDays++;
            }
        }
        return result;
    }
}
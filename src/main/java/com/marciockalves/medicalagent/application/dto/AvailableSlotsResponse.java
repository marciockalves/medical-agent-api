package com.marciockalves.medicalagent.application.dto;

import java.util.List;

public record AvailableSlotsResponse(
        String doctorName,
        String periodDescription,
        List<String> morningSlots,
        List<String> afternoonSlots
) {}
package com.marciockalves.medicalagent.domain.valueobject;

import java.time.LocalTime;

public record TimeRange(LocalTime start, LocalTime end) {
}
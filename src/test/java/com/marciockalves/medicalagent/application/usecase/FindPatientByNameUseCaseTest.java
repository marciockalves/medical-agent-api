package com.marciockalves.medicalagent.application.usecase;

import com.marciockalves.medicalagent.application.dto.PatientStatusDTO;
import com.marciockalves.medicalagent.domain.entity.Patient;
import com.marciockalves.medicalagent.domain.port.PatientRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindPatientByNameUseCaseTest {

    @Mock
    private PatientRepositoryPort patientRepository;

    @InjectMocks
    private FindPatientByNameUseCase useCase;

    @Test
    @DisplayName("Deve retornar não encontrado quando o paciente não existir no banco")
    void shouldReturnNotFoundWhenPatientDoesNotExist() {
        // Arrange
        String name = "Paciente Inexistente";
        when(patientRepository.findByFullNameContainingIgnoreCase(name))
                .thenReturn(Collections.emptyList());

        // Act
        PatientStatusDTO result = useCase.execute(name);

        // Assert
        assertFalse(result.found());
        assertEquals("Nenhum paciente encontrado com o nome: " + name, result.message());
        verify(patientRepository, times(1)).findByFullNameContainingIgnoreCase(name);
    }

    @Test
    @DisplayName("Deve indicar necessidade de atualização quando a última alteração for superior a 6 meses")
    void shouldRequireUpdateWhenLastUpdateIsOlderThanSixMonths() {
        UUID patientId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String name = "João Silva";
        Patient patient = Patient.builder()
                .id(patientId)
                .fullName("João Silva")
                .updatedAt(LocalDate.now().minusMonths(7).atStartOfDay()) // 7 meses atrás
                .build();

        when(patientRepository.findByFullNameContainingIgnoreCase(name))
                .thenReturn(List.of(patient));

        // Act
        PatientStatusDTO result = useCase.execute(name);

        // Assert
        assertTrue(result.found());
        assertTrue(result.needsUpdate());
        assertEquals("Paciente encontrado. O cadastro necessita de atualização (última atualização há mais de 6 meses).", result.message());
    }

    @Test
    @DisplayName("Deve confirmar cadastro atualizado quando a alteração for inferior a 6 meses")
    void shouldNotRequireUpdateWhenLastUpdateIsRecent() {
        UUID patientId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String name = "Maria Souza";
        Patient patient = Patient.builder()
                .id(patientId)
                .fullName("Maria Souza")
                .updatedAt(LocalDate.now().minusMonths(2).atStartOfDay()) // 2 meses atrás
                .build();

        when(patientRepository.findByFullNameContainingIgnoreCase(name))
                .thenReturn(List.of(patient));

        // Act
        PatientStatusDTO result = useCase.execute(name);

        // Assert
        assertTrue(result.found());
        assertFalse(result.needsUpdate());
        assertEquals("Paciente encontrado com cadastro atualizado.", result.message());
    }
}
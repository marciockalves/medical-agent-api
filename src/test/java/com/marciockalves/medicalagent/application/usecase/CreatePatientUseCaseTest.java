package com.marciockalves.medicalagent.application.usecase;


import com.marciockalves.medicalagent.application.dto.CreatePatientInput;
import com.marciockalves.medicalagent.application.dto.PatientOutput;
import com.marciockalves.medicalagent.domain.entity.Patient;
import com.marciockalves.medicalagent.domain.port.PatientRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePatientUseCaseTest {

    @Mock
    private PatientRepositoryPort patientRepositoryPort;

    @InjectMocks
    private CreatePatientUseCase createPatientUseCase;

    private CreatePatientInput input;

    @BeforeEach
    void setUp() {
        input = new CreatePatientInput(
                "Marcio Alves",
                "123.456.789-00",
                LocalDate.of(1990, 5, 15),
                "marcio@email.com",
                "11999998888"
        );
    }

    @Test
    @DisplayName("Deve criar um paciente com sucesso quando os dados forem válidos")
    void shouldCreatePatientSuccessfully() {
        // Arrange
        when(patientRepositoryPort.existsByCpf("12345678900")).thenReturn(false);
        when(patientRepositoryPort.existsByEmail("marcio@email.com")).thenReturn(false);

        Patient savedPatient = Patient.builder()
                .id(UUID.randomUUID())
                .fullName(input.fullName())
                .cpf("12345678900")
                .birthDate(input.birthDate())
                .email(input.email())
                .phone(input.phone())
                .build();

        when(patientRepositoryPort.save(any(Patient.class))).thenReturn(savedPatient);

        // Act
        PatientOutput output = createPatientUseCase.execute(input);

        // Assert
        assertNotNull(output);
        assertNotNull(output.id());
        assertEquals("Marcio Alves", output.fullName());
        assertEquals("12345678900", output.cpf());
        assertEquals("marcio@email.com", output.email());

        verify(patientRepositoryPort, times(1)).existsByCpf("12345678900");
        verify(patientRepositoryPort, times(1)).existsByEmail("marcio@email.com");
        verify(patientRepositoryPort, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o CPF já estiver cadastrado")
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        // Arrange
        when(patientRepositoryPort.existsByCpf("12345678900")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createPatientUseCase.execute(input)
        );

        assertEquals("Já existe um paciente cadastrado com este CPF.", exception.getMessage());
        verify(patientRepositoryPort, times(1)).existsByCpf("12345678900");
        verify(patientRepositoryPort, never()).existsByEmail(any());
        verify(patientRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o e-mail já estiver cadastrado")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        when(patientRepositoryPort.existsByCpf("12345678900")).thenReturn(false);
        when(patientRepositoryPort.existsByEmail("marcio@email.com")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createPatientUseCase.execute(input)
        );

        assertEquals("Já existe um paciente cadastrado com este e-mail.", exception.getMessage());
        verify(patientRepositoryPort, times(1)).existsByCpf("12345678900");
        verify(patientRepositoryPort, times(1)).existsByEmail("marcio@email.com");
        verify(patientRepositoryPort, never()).save(any());
    }
}
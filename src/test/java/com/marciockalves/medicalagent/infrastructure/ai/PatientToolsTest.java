package com.marciockalves.medicalagent.infrastructure.ai;

import com.marciockalves.medicalagent.application.dto.PatientStatusDTO;
import com.marciockalves.medicalagent.application.usecase.FindPatientByNameUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientToolsTest {

    @Mock
    private FindPatientByNameUseCase findPatientByNameUseCase; // 👈 Mockamos o USE CASE (não mais o repository)

    @InjectMocks
    private PatientTools patientTools; // 👈 Injeta o Use Case mockado na Tool

    @Test
    @DisplayName("Deve chamar o UseCase de busca de paciente e retornar a String do DTO para a IA")
    void shouldCallUseCaseAndReturnPatientStatusString() {
        // Arrange
        String name = "Márcio Alves";
        PatientStatusDTO mockDto = PatientStatusDTO.builder()
                .found(true)
                .patientId(UUID.randomUUID())
                .fullName("Márcio Alves")
                .needsUpdate(false)
                .message("Paciente encontrado com cadastro atualizado.")
                .build();

        when(findPatientByNameUseCase.execute(name)).thenReturn(mockDto);

        // Act
        String result = patientTools.searchPatientByName(name);

        // Assert
        assertTrue(result.contains("Márcio Alves"));
        assertTrue(result.contains("Paciente encontrado com cadastro atualizado."));
        verify(findPatientByNameUseCase, times(1)).execute(name); // Garante que a Tool chamou o Use Case
    }

    @Test
    @DisplayName("Deve delegar ao UseCase quando o paciente não for encontrado")
    void shouldDelegateToUseCaseWhenPatientNotFound() {
        // Arrange
        String name = "Inexistente";
        PatientStatusDTO notFoundDto = PatientStatusDTO.notFound(name);

        when(findPatientByNameUseCase.execute(name)).thenReturn(notFoundDto);

        // Act
        String result = patientTools.searchPatientByName(name);

        // Assert
        assertTrue(result.contains("Nenhum paciente encontrado com o nome: Inexistente"));
        verify(findPatientByNameUseCase, times(1)).execute(name);
    }
}
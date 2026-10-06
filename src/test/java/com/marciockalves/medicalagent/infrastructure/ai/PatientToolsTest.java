package com.marciockalves.medicalagent.infrastructure.ai;

import com.marciockalves.medicalagent.application.dto.PatientStatusDTO;
import com.marciockalves.medicalagent.application.usecase.FindPatientByNameUseCase;
import com.marciockalves.medicalagent.application.dto.CreatePatientInput;
import com.marciockalves.medicalagent.application.dto.PatientOutput;
import com.marciockalves.medicalagent.application.usecase.CreatePatientUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientToolsTest {

    @Mock
    private FindPatientByNameUseCase findPatientByNameUseCase;

    @Mock
    private CreatePatientUseCase createPatientUseCase; // 👈 Mock do novo Use Case de criação

    @InjectMocks
    private PatientTools patientTools;

    // --- TESTES DE BUSCA ---

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
        verify(findPatientByNameUseCase, times(1)).execute(name);
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

    // --- TESTES DE CRIAÇÃO ---

    @Test
    @DisplayName("Deve chamar o CreatePatientUseCase e retornar a mensagem de sucesso para a IA")
    void shouldCallCreatePatientUseCaseSuccessfully() {
        // Arrange
        UUID generatedId = UUID.randomUUID();
        PatientTools.CreatePatientRequest request = new PatientTools.CreatePatientRequest(
                "Márcio Alves",
                "12345678900",
                "1990-05-15",
                "marcio@email.com",
                "11999998888"
        );

        PatientOutput mockOutput = new PatientOutput(
                generatedId,
                "Márcio Alves",
                "12345678900",
                LocalDate.of(1990, 5, 15),
                "marcio@email.com",
                "11999998888"
        );

        when(createPatientUseCase.execute(any(CreatePatientInput.class))).thenReturn(mockOutput);

        // Act
        String result = patientTools.createPatient(request);

        // Assert
        assertTrue(result.contains("Paciente Márcio Alves cadastrado com sucesso!"));
        assertTrue(result.contains(generatedId.toString()));
        verify(createPatientUseCase, times(1)).execute(any(CreatePatientInput.class));
    }

    @Test
    @DisplayName("Deve capturar exceção do UseCase e retornar mensagem de erro formatada para a IA")
    void shouldCatchExceptionAndReturnErrorMessage() {
        // Arrange
        PatientTools.CreatePatientRequest request = new PatientTools.CreatePatientRequest(
                "Márcio Alves",
                "12345678900",
                "1990-05-15",
                "marcio@email.com",
                "11999998888"
        );

        when(createPatientUseCase.execute(any(CreatePatientInput.class)))
                .thenThrow(new IllegalArgumentException("Já existe um paciente cadastrado com este CPF."));

        // Act
        String result = patientTools.createPatient(request);

        // Assert
        assertTrue(result.contains("Erro ao realizar o cadastro: Já existe um paciente cadastrado com este CPF."));
        verify(createPatientUseCase, times(1)).execute(any(CreatePatientInput.class));
    }
}
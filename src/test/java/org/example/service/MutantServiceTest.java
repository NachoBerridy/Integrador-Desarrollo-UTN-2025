package org.example.service;

import org.example.entity.DnaRecord;
import org.example.repository.DnaRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test unitarios para MutantService
 * Total: 5 tests
 */
@ExtendWith(MockitoExtension.class)
class MutantServiceTest {

    @Mock
    private MutantDetector mutantDetector;

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private MutantService mutantService;

    private String[] mutantDna;
    private String[] humanDna;

    @BeforeEach
    void setUp() {
        mutantDna = new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
        humanDna = new String[]{"ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG"};
    }

    @Test
    @DisplayName("Debe analizar DNA mutante y guardarlo en BD")
    void testIsMutant_NewMutantDna_ShouldSaveToDatabase() {
        // Arrange
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(mutantDna)).thenReturn(true);
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenReturn(new DnaRecord());

        // Act
        boolean result = mutantService.isMutant(mutantDna);

        // Assert
        assertTrue(result, "Debe retornar true para DNA mutante");
        verify(mutantDetector, times(1)).isMutant(mutantDna);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Debe analizar DNA humano y guardarlo en BD")
    void testIsMutant_NewHumanDna_ShouldSaveToDatabase() {
        // Arrange
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(humanDna)).thenReturn(false);
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenReturn(new DnaRecord());

        // Act
        boolean result = mutantService.isMutant(humanDna);

        // Assert
        assertFalse(result, "Debe retornar false para DNA humano");
        verify(mutantDetector, times(1)).isMutant(humanDna);
        verify(dnaRecordRepository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Debe retornar resultado cacheado sin re-analizar")
    void testIsMutant_ExistingDna_ShouldReturnCachedResult() {
        // Arrange
        DnaRecord existingRecord = DnaRecord.builder()
                .id(1L)
                .dnaHash("hash123")
                .isMutant(true)
                .build();
        
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.of(existingRecord));

        // Act
        boolean result = mutantService.isMutant(mutantDna);

        // Assert
        assertTrue(result, "Debe retornar resultado cacheado");
        verify(dnaRecordRepository, times(1)).findByDnaHash(anyString());
        verify(mutantDetector, never()).isMutant(any()); // No debe llamar al detector
        verify(dnaRecordRepository, never()).save(any()); // No debe guardar
    }

    @Test
    @DisplayName("Debe generar hash diferente para DNA diferente")
    void testIsMutant_DifferentDna_ShouldGenerateDifferentHash() {
        // Arrange
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(any())).thenReturn(true);
        when(dnaRecordRepository.save(any(DnaRecord.class))).thenReturn(new DnaRecord());

        // Act
        mutantService.isMutant(mutantDna);
        mutantService.isMutant(humanDna);

        // Assert
        verify(dnaRecordRepository, times(2)).findByDnaHash(anyString());
        verify(dnaRecordRepository, times(2)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Debe generar mismo hash para mismo DNA")
    void testIsMutant_SameDna_ShouldGenerateSameHash() {
        // Arrange
        DnaRecord existingRecord = DnaRecord.builder()
                .id(1L)
                .dnaHash("hash123")
                .isMutant(true)
                .build();
        
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.of(existingRecord));

        // Act
        boolean result1 = mutantService.isMutant(mutantDna);
        boolean result2 = mutantService.isMutant(mutantDna);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        verify(dnaRecordRepository, times(2)).findByDnaHash(anyString());
        verify(mutantDetector, never()).isMutant(any()); // No debe analizar ninguna vez
    }
}

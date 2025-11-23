package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.DnaRequest;
import org.example.service.MutantService;
import org.example.service.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para MutantController
 * Total: 8 tests
 */
@WebMvcTest(MutantController.class)
class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MutantService mutantService;

    @MockBean
    private StatsService statsService;

    private final String[] mutantDna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
    private final String[] humanDna = {"ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG"};

    // ========================================
    // POST /mutant - Casos exitosos
    // ========================================

    @Test
    @DisplayName("POST /mutant debe retornar 200 OK para DNA mutante")
    void testCheckMutant_MutantDna_Returns200() throws Exception {
        // Arrange
        DnaRequest request = new DnaRequest();
        request.setDna(mutantDna);
        when(mutantService.isMutant(any(String[].class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 403 Forbidden para DNA humano")
    void testCheckMutant_HumanDna_Returns403() throws Exception {
        // Arrange
        DnaRequest request = new DnaRequest();
        request.setDna(humanDna);
        when(mutantService.isMutant(any(String[].class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ========================================
    // POST /mutant - Validaciones
    // ========================================

    @Test
    @DisplayName("POST /mutant debe retornar 400 para DNA null")
    void testCheckMutant_NullDna_Returns400() throws Exception {
        // Arrange
        DnaRequest request = new DnaRequest();
        request.setDna(null);

        // Act & Assert
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 para matriz no cuadrada")
    void testCheckMutant_NonSquareMatrix_Returns400() throws Exception {
        // Arrange
        DnaRequest request = new DnaRequest();
        request.setDna(new String[]{"ATGC", "CAGT", "TTAT"}); // 3x4

        // Act & Assert
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 para caracteres inválidos")
    void testCheckMutant_InvalidCharacters_Returns400() throws Exception {
        // Arrange
        DnaRequest request = new DnaRequest();
        request.setDna(new String[]{"ATXC", "CAGT", "TTAT", "AGAC"}); // X es inválido

        // Act & Assert
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 para matriz menor a 4x4")
    void testCheckMutant_TooSmallMatrix_Returns400() throws Exception {
        // Arrange
        DnaRequest request = new DnaRequest();
        request.setDna(new String[]{"ATG", "CAG", "TTA"}); // 3x3

        // Act & Assert
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ========================================
    // GET /stats
    // ========================================

    @Test
    @DisplayName("GET /stats debe retornar 200 con estadísticas")
    void testGetStats_ReturnsStatsSuccessfully() throws Exception {
        // Arrange
        org.example.dto.StatsResponse statsResponse = org.example.dto.StatsResponse.builder()
                .countMutantDna(40L)
                .countHumanDna(100L)
                .ratio(0.4)
                .build();
        when(statsService.getStats()).thenReturn(statsResponse);

        // Act & Assert
        mockMvc.perform(get("/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(40))
                .andExpect(jsonPath("$.count_human_dna").value(100))
                .andExpect(jsonPath("$.ratio").value(0.4));
    }

    @Test
    @DisplayName("GET /stats debe retornar Content-Type application/json")
    void testGetStats_ReturnsJsonContentType() throws Exception {
        // Arrange
        org.example.dto.StatsResponse statsResponse = org.example.dto.StatsResponse.builder()
                .countMutantDna(0L)
                .countHumanDna(0L)
                .ratio(0.0)
                .build();
        when(statsService.getStats()).thenReturn(statsResponse);

        // Act & Assert
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}

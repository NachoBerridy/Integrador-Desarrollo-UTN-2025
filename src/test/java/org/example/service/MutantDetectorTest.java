package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitarios para MutantDetector
 * Objetivo: >95% de cobertura (4 puntos en rúbrica)
 * Total: 17 tests
 */
class MutantDetectorTest {

    private MutantDetector mutantDetector;

    @BeforeEach
    void setUp() {
        mutantDetector = new MutantDetector();
    }

    // ========================================
    // CASOS MUTANTES (debe retornar true)
    // ========================================

    @Test
    @DisplayName("Mutante con secuencias horizontal y diagonal")
    void testMutantWithHorizontalAndDiagonalSequences() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA", // Horizontal: CCCC
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna), "Debe detectar mutante con 2+ secuencias");
    }

    @Test
    @DisplayName("Mutante con secuencias verticales")
    void testMutantWithVerticalSequences() {
        String[] dna = {
                "ATGCGA",
                "ATGTGC",
                "ATATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna), "Debe detectar mutante con secuencias verticales");
    }

    @Test
    @DisplayName("Mutante con múltiples secuencias horizontales")
    void testMutantWithMultipleHorizontalSequences() {
        String[] dna = {
                "AAAAGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna), "Debe detectar mutante con múltiples horizontales");
    }

    @Test
    @DisplayName("Mutante con diagonales ascendentes y descendentes")
    void testMutantWithBothDiagonals() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCGCTA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna), "Debe detectar mutante con diagonales");
    }

    @Test
    @DisplayName("Mutante con matriz grande (10x10)")
    void testMutantWithLargeDna() {
        String[] dna = {
                "ATGCGATGCA",
                "CAGTGCAGTG",
                "TTATGTTTAT",
                "AGAAGGAGAA",
                "CCCCTACCCC",
                "TCACTGTCAC",
                "ATGCGATGCA",
                "CAGTGCAGTG",
                "TTATGTTTAT",
                "AGAAGGAGAA"
        };
        assertTrue(mutantDetector.isMutant(dna), "Debe manejar matrices grandes");
    }

    @Test
    @DisplayName("Mutante con todos los caracteres iguales")
    void testMutantAllSameCharacter() {
        String[] dna = {
                "AAAA",
                "AAAA",
                "AAAA",
                "AAAA"
        };
        assertTrue(mutantDetector.isMutant(dna), "Debe detectar mutante con todos iguales");
    }

    @Test
    @DisplayName("Mutante con secuencia diagonal descendente en esquina")
    void testMutantDiagonalInCorner() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTACGT",
                "AGACGG",
                "CCGCTA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna), "Debe detectar diagonal en esquinas");
    }

    // ========================================
    // CASOS HUMANOS (debe retornar false)
    // ========================================

    @Test
    @DisplayName("Humano con solo una secuencia")
    void testNotMutantWithOnlyOneSequence() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATTT", // Solo una secuencia: TTT
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };
        assertFalse(mutantDetector.isMutant(dna), "No debe detectar mutante con solo 1 secuencia");
    }

    @Test
    @DisplayName("Humano sin secuencias")
    void testNotMutantWithNoSequences() {
        String[] dna = {
                "ATGC",
                "CAGT",
                "TTAT",
                "AGAC"
        };
        assertFalse(mutantDetector.isMutant(dna), "No debe detectar mutante sin secuencias");
    }

    @Test
    @DisplayName("Humano con matriz 4x4 sin secuencias")
    void testNotMutantSmallDna() {
        String[] dna = {
                "ATGC",
                "CAGT",
                "TTAC",
                "AGAC"
        };
        assertFalse(mutantDetector.isMutant(dna), "No debe detectar mutante en 4x4 sin secuencias");
    }

    // ========================================
    // VALIDACIONES (debe retornar false)
    // ========================================

    @Test
    @DisplayName("Debe retornar false para DNA null")
    void testNotMutantWithNullDna() {
        assertFalse(mutantDetector.isMutant(null), "Debe manejar DNA null correctamente");
    }

    @Test
    @DisplayName("Debe retornar false para array vacío")
    void testNotMutantWithEmptyDna() {
        String[] dna = {};
        assertFalse(mutantDetector.isMutant(dna), "Debe manejar array vacío correctamente");
    }

    @Test
    @DisplayName("Debe retornar false para matriz no cuadrada")
    void testNotMutantWithNonSquareDna() {
        String[] dna = {
                "ATGC",
                "CAGT",
                "TTAT"  // Solo 3 filas
        };
        assertFalse(mutantDetector.isMutant(dna), "Debe rechazar matriz no cuadrada");
    }

    @Test
    @DisplayName("Debe manejar correctamente matriz con caracteres mezclados")
    void testNotMutantWithMixedCharacters() {
        String[] dna = {
                "ATGC",
                "CGTA",
                "TACG",
                "GCAT"
        };
        assertFalse(mutantDetector.isMutant(dna), "No debe detectar mutante sin secuencias");
    }

    @Test
    @DisplayName("Debe retornar false para matriz muy pequeña (3x3)")
    void testNotMutantWithTooSmallDna() {
        String[] dna = {
                "ATG",
                "CAG",
                "TTA"
        };
        assertFalse(mutantDetector.isMutant(dna), "Debe rechazar matriz < 4x4");
    }

    // ========================================
    // EDGE CASES
    // ========================================

    @Test
    @DisplayName("Debe detectar múltiples secuencias superpuestas")
    void testMutantWithOverlappingSequences() {
        String[] dna = {
                "AAAAAA",  // Tiene múltiples secuencias de 4 superpuestas
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",  // Otra secuencia horizontal
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna), 
                "Debe detectar mutante con secuencias superpuestas");
    }

    @Test
    @DisplayName("Mutante con exactamente 2 secuencias (caso límite)")
    void testMutantWithExactlyTwoSequences() {
        String[] dna = {
                "AAAATC",
                "TGCAGT",
                "GCTTCC",
                "CCCCTG",
                "GTAGTC",
                "AGTCAC"
        };
        assertTrue(mutantDetector.isMutant(dna), "Debe detectar mutante con exactamente 2 secuencias");
    }
}

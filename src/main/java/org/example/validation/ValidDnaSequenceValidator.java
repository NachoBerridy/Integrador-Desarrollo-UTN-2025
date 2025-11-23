package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDnaSequenceValidator implements ConstraintValidator<ValidDnaSequence, String[]> {

    @Override
    public boolean isValid(String[] dna, ConstraintValidatorContext context) {
        if (dna == null) {
            return false;
        }

        int n = dna.length;
        
        // Minimum size is 4x4 to form sequences of 4
        if (n < 4) {
            return false;
        }

        for (String row : dna) {
            if (row == null || row.length() != n) {
                return false; // Not a square matrix
            }
            for (char c : row.toCharArray()) {
                if (c != 'A' && c != 'T' && c != 'C' && c != 'G') {
                    return false; // Invalid character
                }
            }
        }

        return true;
    }
}

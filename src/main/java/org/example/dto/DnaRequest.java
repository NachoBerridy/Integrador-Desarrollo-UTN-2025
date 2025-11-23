package org.example.dto;

import java.io.Serializable;

import org.example.validation.ValidDnaSequence;

import lombok.Data;

@Data
public class DnaRequest implements Serializable {
    @ValidDnaSequence
    private String[] dna;
}

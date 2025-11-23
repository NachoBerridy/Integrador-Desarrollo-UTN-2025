package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.DnaRecord;
import org.example.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MutantService {

    private final MutantDetector mutantDetector;
    private final DnaRecordRepository dnaRecordRepository;

    public boolean isMutant(String[] dna) {
        String dnaHash = calculateDnaHash(dna);

        // Check if already analyzed
        Optional<DnaRecord> existingRecord = dnaRecordRepository.findByDnaHash(dnaHash);
        if (existingRecord.isPresent()) {
            return existingRecord.get().isMutant();
        }

        // Analyze
        boolean isMutant = mutantDetector.isMutant(dna);

        // Save result
        DnaRecord record = DnaRecord.builder()
                .dnaHash(dnaHash)
                .isMutant(isMutant)
                .build();
        dnaRecordRepository.save(record);

        return isMutant;
    }

    private String calculateDnaHash(String[] dna) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();
            for (String s : dna) {
                sb.append(s);
            }
            byte[] encodedhash = digest.digest(sb.toString().getBytes());
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating DNA hash", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

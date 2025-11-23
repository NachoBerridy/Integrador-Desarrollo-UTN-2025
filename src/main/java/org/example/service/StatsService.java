package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.StatsResponse;
import org.example.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final DnaRecordRepository dnaRecordRepository;

    public StatsResponse getStats() {
        long countMutantDna = dnaRecordRepository.countByIsMutant(true);
        long countHumanDna = dnaRecordRepository.countByIsMutant(false);
        double ratio = 0.0;

        if (countHumanDna > 0) {
            ratio = (double) countMutantDna / countHumanDna;
        } else if (countMutantDna > 0) {
            ratio = countMutantDna; // Or handle as infinity/max value if preferred
        }

        return StatsResponse.builder()
                .countMutantDna(countMutantDna)
                .countHumanDna(countHumanDna)
                .ratio(ratio)
                .build();
    }
}

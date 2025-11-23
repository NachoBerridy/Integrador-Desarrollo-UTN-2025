package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.DnaRequest;
import org.example.dto.StatsResponse;
import org.example.service.MutantService;
import org.example.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MutantController {

    private final MutantService mutantService;
    private final StatsService statsService;

    @Operation(summary = "Detect if a human is a mutant based on their DNA sequence")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Is a Mutant"),
            @ApiResponse(responseCode = "403", description = "Is Human"),
            @ApiResponse(responseCode = "400", description = "Invalid DNA sequence")
    })
    @PostMapping("/mutant")
    public ResponseEntity<Void> checkMutant(@Valid @RequestBody DnaRequest dnaRequest) {
        boolean isMutant = mutantService.isMutant(dnaRequest.getDna());
        if (isMutant) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Operation(summary = "Get statistics of DNA verifications")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(statsService.getStats());
    }
}

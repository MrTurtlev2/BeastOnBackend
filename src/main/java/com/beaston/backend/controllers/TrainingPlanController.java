package com.beaston.backend.controllers;

import com.beaston.backend.DTO.TrainingPlanDTO;
import com.beaston.backend.DTO.TrainingPlanResponseDTO;
import com.beaston.backend.DTO.api.ApiErrorResponseDto;
import com.beaston.backend.DTO.api.ApiSuccessResponseDto;
import com.beaston.backend.entities.TrainingPlan;
import com.beaston.backend.enums.ErrorTypeEnum;
import com.beaston.backend.services.CustomerService;
import com.beaston.backend.services.TrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/training-plans")
public class TrainingPlanController {
    private final CustomerService customerService;
    @Autowired
    private TrainingPlanService trainingPlanService;

    public TrainingPlanController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/add-plan")
    public ResponseEntity<TrainingPlan> addTrainingPlan(@RequestBody TrainingPlanDTO trainingPlanDTO) {
        Long customerId = customerService.getAuthenticatedCustomerId();

        TrainingPlan savedTrainingPlan = trainingPlanService.addTrainingPlan(customerId, trainingPlanDTO);
        return ResponseEntity.ok(savedTrainingPlan);
    }


    @GetMapping("/my-plans")
    public ResponseEntity<List<TrainingPlanResponseDTO>> getMyTrainingPlans() {
        Long customerId = customerService.getAuthenticatedCustomerId();
        List<TrainingPlanResponseDTO> plans = trainingPlanService.getTrainingPlansByCustomerId(customerId);
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/{planId}/assign-exercises")
    public ResponseEntity<Object> addExerciseToPlan(
            @PathVariable Long planId, // ID planu treningowego
            @RequestParam Long exerciseId) { // ID ćwiczenia przypisanego do użytkownika

        try {
            // Wywołanie serwisu do przypisania ćwiczenia
            trainingPlanService.assignExerciseToTrainingPlan(planId, exerciseId);

            // Odpowiedź potwierdzająca
            ApiSuccessResponseDto response = new ApiSuccessResponseDto("Ćwiczenie zostało przypisane do planu treningowego");
            return ResponseEntity.ok(response);

        } catch (ResponseStatusException e) {
            // Obsługa błędów
            ApiErrorResponseDto response = new ApiErrorResponseDto(
                    e.getMessage(),
                    ErrorTypeEnum.BUSINESS_ERROR,
                    HttpStatus.BAD_REQUEST.value()
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            // Ogólna obsługa błędów
            ApiErrorResponseDto response = new ApiErrorResponseDto(
                    "Błąd serwera",
                    ErrorTypeEnum.SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
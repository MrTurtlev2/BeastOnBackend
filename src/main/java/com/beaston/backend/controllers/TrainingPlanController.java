package com.beaston.backend.controllers;

import com.beaston.backend.DTO.TrainingPlanDTO;
import com.beaston.backend.DTO.TrainingPlanResponseDTO;
import com.beaston.backend.DTO.exercises.AssignExerciseDto;
import com.beaston.backend.entities.*;
import com.beaston.backend.repositories.CustomerExerciseRepository;
import com.beaston.backend.repositories.CustomerRepository;
import com.beaston.backend.repositories.ExerciseRepository;
import com.beaston.backend.repositories.TrainingPlanRepository;
import com.beaston.backend.services.CustomerService;
import com.beaston.backend.services.TrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training-plans")
public class TrainingPlanController {

    private final CustomerService customerService;

    @Autowired
    private TrainingPlanService trainingPlanService;

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerExerciseRepository customerExerciseRepository;

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


    @PostMapping("/{trainingPlanId}/assign-exercise")
    public ResponseEntity<?> addExerciseToTrainingPlan(
            @PathVariable Long trainingPlanId,
            @RequestBody AssignExerciseDto request
    ) {

        Long customerId = customerService.getAuthenticatedCustomerId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // 1. Znalezienie ćwiczenia globalnego
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        // 2. Dodanie ćwiczenia do użytkownika (CustomerExercise)
        CustomerExercise customerExercise = new CustomerExercise();
        customerExercise.setCustomer(customer);
        customerExercise.setExercise(exercise);
        customerExercise.setWeight(request.getWeight());
        customerExercise.setRepetitions(request.getRepetitions());

        customerExerciseRepository.save(customerExercise);

        // 3. Przypisanie ćwiczenia do planu treningowego
        TrainingPlan trainingPlan = trainingPlanRepository.findById(trainingPlanId)
                .orElseThrow(() -> new RuntimeException("Training plan not found"));

        TrainingPlanExercise trainingPlanExercise = new TrainingPlanExercise();
        trainingPlanExercise.setTrainingPlan(trainingPlan);
        trainingPlanExercise.setCustomerExercise(customerExercise);
        trainingPlanExercise.setOrderIndex(trainingPlan.getTrainingPlanExercises().size() + 1); // Automatyczne ustawienie kolejności

        trainingPlan.getTrainingPlanExercises().add(trainingPlanExercise);
        trainingPlanRepository.save(trainingPlan);

        return ResponseEntity.ok("Exercise added to training plan");
    }


//    @PostMapping("/{planId}/assign-exercises")
//    public ResponseEntity<Object> addExerciseToPlan(
//            @PathVariable Long planId,
//            @RequestBody Long exerciseId) {
//
//        Long customerId = customerService.getAuthenticatedCustomerId();
//
//        try {
//            // Wywołanie serwisu do przypisania ćwiczenia
//            trainingPlanService.assignExerciseToTrainingPlan(planId, exerciseId, customerId);
//
//            // Odpowiedź potwierdzająca
//            ApiSuccessResponseDto response = new ApiSuccessResponseDto("Ćwiczenie zostało przypisane do planu treningowego");
//            return ResponseEntity.ok(response);
//
//        } catch (ResponseStatusException e) {
//            // Obsługa błędów
//            ApiErrorResponseDto response = new ApiErrorResponseDto(
//                    e.getMessage(),
//                    ErrorTypeEnum.BUSINESS_ERROR,
//                    HttpStatus.BAD_REQUEST.value()
//            );
//            return ResponseEntity.badRequest().body(response);
//        } catch (Exception e) {
//            // Ogólna obsługa błędów
//            ApiErrorResponseDto response = new ApiErrorResponseDto(
//                    "Błąd serwera",
//                    ErrorTypeEnum.SERVER_ERROR,
//                    HttpStatus.INTERNAL_SERVER_ERROR.value()
//            );
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }

}
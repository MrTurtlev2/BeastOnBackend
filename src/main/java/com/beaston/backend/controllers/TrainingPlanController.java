package com.beaston.backend.controllers;

import com.beaston.backend.DTO.TrainingPlanDTO;
import com.beaston.backend.DTO.TrainingPlanResponseDTO;
import com.beaston.backend.entities.TrainingPlan;
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
}
package com.beaston.backend.controllers;

import com.beaston.backend.DTO.TrainingPlanDTO;
import com.beaston.backend.entities.TrainingPlan;
import com.beaston.backend.services.TrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/training-plans")
public class TrainingPlanController {
    @Autowired
    private TrainingPlanService trainingPlanService;

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<TrainingPlan> addTrainingPlan(@PathVariable Long customerId,
                                                        @RequestBody TrainingPlanDTO trainingPlanDTO) {
        TrainingPlan savedTrainingPlan = trainingPlanService.addTrainingPlan(customerId, trainingPlanDTO);
        return ResponseEntity.ok(savedTrainingPlan);
    }
}


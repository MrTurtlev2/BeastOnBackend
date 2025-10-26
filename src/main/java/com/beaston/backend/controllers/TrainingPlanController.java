package com.beaston.backend.controllers;

import com.beaston.backend.DTO.TrainingPlanDTO;
import com.beaston.backend.DTO.WeeklyPlanResponseDTO;
import com.beaston.backend.services.CustomerService;
import com.beaston.backend.services.TrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training-plans")
public class TrainingPlanController {

    @Autowired
    private TrainingPlanService trainingPlanService;

    @Autowired
    private CustomerService customerService;

    @PostMapping("/add-plan")
    public ResponseEntity<?> createPlan(@RequestBody TrainingPlanDTO dto) {
        return ResponseEntity.ok(trainingPlanService.createPlan(dto));
    }

    @GetMapping("/weekly-schedule")
    public ResponseEntity<List<WeeklyPlanResponseDTO>> getWeeklySchedule() {
        Long customerId = customerService.getAuthenticatedCustomerId();
        return ResponseEntity.ok(trainingPlanService.getWeeklySchedule(customerId));
    }

}

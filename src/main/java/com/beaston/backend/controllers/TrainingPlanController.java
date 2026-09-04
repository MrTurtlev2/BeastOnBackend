package com.beaston.backend.controllers;

import com.beaston.backend.DTO.TrainingPlanDTO;
import com.beaston.backend.DTO.WeeklyPlanResponseDTO;
import com.beaston.backend.DTO.plans.AssignPlanToDayDTO;
import com.beaston.backend.DTO.plans.UpdateTrainingPlanDTO;
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

    @PostMapping("/assign-plan-to-day")
    public ResponseEntity<Void> assignPlanToDay(@RequestBody AssignPlanToDayDTO dto) {
        Long customerId = customerService.getAuthenticatedCustomerId();

        trainingPlanService.assignPlanToDay(customerId, dto);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-plan/{uuid}")
    public ResponseEntity<Void> updatePlan(
            @PathVariable String uuid,
            @RequestBody UpdateTrainingPlanDTO dto
    ) {
        Long customerId = customerService.getAuthenticatedCustomerId();

        trainingPlanService.updatePlan(customerId, uuid, dto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove-plan/{uuid}")
    public ResponseEntity<Void> removePlan(@PathVariable String uuid) {
        Long customerId = customerService.getAuthenticatedCustomerId();

        trainingPlanService.removePlan(customerId, uuid);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/weekly-schedule")
    public ResponseEntity<List<WeeklyPlanResponseDTO>> getWeeklySchedule() {
        Long customerId = customerService.getAuthenticatedCustomerId();
        return ResponseEntity.ok(trainingPlanService.getWeeklySchedule(customerId));
    }

    ///  not used anymore
    //    @PostMapping("/add-exercise-to-plan/{planId}")
    //    public ResponseEntity<TrainingPlan> addExerciseToPlan(
    //            @PathVariable Long planId,
    //            @RequestBody ExerciseDTO dto
    //    ) {
    //        TrainingPlan updatedPlan = trainingPlanService.addExerciseToPlan(planId, dto);
    //        return ResponseEntity.ok(updatedPlan);
    //    }

}

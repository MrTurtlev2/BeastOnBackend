package com.beaston.backend.controllers;

import com.beaston.backend.DTO.ExerciseDTO;
import com.beaston.backend.DTO.ExerciseSetDTO;
import com.beaston.backend.DTO.TrainingPlanDTO;
import com.beaston.backend.entities.ExerciseSet;
import com.beaston.backend.entities.TrainingPlan;
import com.beaston.backend.entities.TrainingPlanExercise;
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

    // ----------------- PLANY -----------------
    @PostMapping("/add-plan")
    public ResponseEntity<TrainingPlan> createPlan(@RequestBody TrainingPlanDTO dto) {
        TrainingPlan plan = trainingPlanService.createPlan(dto);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/my-plans")
    public ResponseEntity<List<TrainingPlan>> getMyPlans() {
        List<TrainingPlan> plans = trainingPlanService.getMyPlans();
        return ResponseEntity.ok(plans);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<?> deletePlan(@PathVariable Long planId) {
        trainingPlanService.deletePlan(planId);
        return ResponseEntity.ok().build();
    }

    // ----------------- ĆWICZENIA -----------------
    @PostMapping("/{planId}/exercises")
    public ResponseEntity<TrainingPlanExercise> addExercise(
            @PathVariable Long planId,
            @RequestBody ExerciseDTO dto
    ) {
        TrainingPlanExercise tpe = trainingPlanService.addExercise(planId, dto);
        return ResponseEntity.ok(tpe);
    }

    @PutMapping("/exercises/{exerciseId}")
    public ResponseEntity<TrainingPlanExercise> updateExercise(
            @PathVariable Long exerciseId,
            @RequestBody ExerciseDTO dto
    ) {
        TrainingPlanExercise tpe = trainingPlanService.updateExercise(exerciseId, dto);
        return ResponseEntity.ok(tpe);
    }

    @DeleteMapping("/exercises/{exerciseId}")
    public ResponseEntity<?> deleteExercise(@PathVariable Long exerciseId) {
        trainingPlanService.deleteExercise(exerciseId);
        return ResponseEntity.ok().build();
    }

    // ----------------- SERIE -----------------
    @PostMapping("/exercises/{exerciseId}/sets")
    public ResponseEntity<ExerciseSet> addSet(
            @PathVariable Long exerciseId,
            @RequestBody ExerciseSetDTO dto
    ) {
        ExerciseSet set = trainingPlanService.addSet(exerciseId, dto);
        return ResponseEntity.ok(set);
    }

    @PutMapping("/sets/{setId}")
    public ResponseEntity<ExerciseSet> updateSet(
            @PathVariable Long setId,
            @RequestBody ExerciseSetDTO dto
    ) {
        ExerciseSet set = trainingPlanService.updateSet(setId, dto);
        return ResponseEntity.ok(set);
    }

    @DeleteMapping("/sets/{setId}")
    public ResponseEntity<?> deleteSet(@PathVariable Long setId) {
        trainingPlanService.deleteSet(setId);
        return ResponseEntity.ok().build();
    }
}

package com.beaston.backend.controllers;

import com.beaston.backend.entities.Exercise;
import com.beaston.backend.services.ExerciseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping
    public ResponseEntity<Exercise> addExercise(@RequestBody Exercise exercise) {
        Exercise savedExercise = exerciseService.addExercise(exercise);
        return ResponseEntity.ok(savedExercise);
    }

    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercises() {
        return ResponseEntity.ok(exerciseService.getAllExercises());
    }
}


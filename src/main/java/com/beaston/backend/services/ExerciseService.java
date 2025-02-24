package com.beaston.backend.services;

import com.beaston.backend.entities.Exercise;
import com.beaston.backend.repositories.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public Exercise addExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }
}


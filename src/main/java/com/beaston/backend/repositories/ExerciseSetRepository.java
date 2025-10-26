package com.beaston.backend.repositories;

import com.beaston.backend.entities.ExerciseSet;
import com.beaston.backend.entities.TrainingPlanExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseSetRepository extends JpaRepository<ExerciseSet, Long> {
    List<ExerciseSet> findByTrainingPlanExercise(TrainingPlanExercise trainingPlanExercise);
}

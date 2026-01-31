package com.beaston.backend.repositories;

import com.beaston.backend.entities.TrainingPlan;
import com.beaston.backend.entities.TrainingPlanExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingPlanExerciseRepository extends JpaRepository<TrainingPlanExercise, Long> {
    List<TrainingPlanExercise> findByTrainingPlan(TrainingPlan trainingPlan);
}

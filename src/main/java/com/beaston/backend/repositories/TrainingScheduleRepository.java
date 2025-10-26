package com.beaston.backend.repositories;

import com.beaston.backend.entities.TrainingPlan;
import com.beaston.backend.entities.TrainingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingScheduleRepository extends JpaRepository<TrainingSchedule, Long> {
    List<TrainingSchedule> findByTrainingPlan(TrainingPlan trainingPlan);
}

package com.beaston.backend.repositories;

import com.beaston.backend.entities.TrainingPlan;
import com.beaston.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
    List<TrainingPlan> findByUser(User user);
}


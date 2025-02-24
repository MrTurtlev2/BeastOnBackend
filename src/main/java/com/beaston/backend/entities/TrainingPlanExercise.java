package com.beaston.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "training_plan_exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingPlanExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "training_plan_id", nullable = false)
    private TrainingPlan trainingPlan;

    @ManyToOne
    @JoinColumn(name = "customer_exercise_id", nullable = false)
    private CustomerExercise customerExercise;

    private Integer orderIndex;
}
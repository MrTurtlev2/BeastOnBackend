package com.beaston.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercise_sets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double weight;
    private Integer repetitions;
    private Integer setNumber;

    @ManyToOne
    @JoinColumn(name = "training_plan_exercise_id", nullable = false)
    @JsonIgnore
    private TrainingPlanExercise trainingPlanExercise;
}

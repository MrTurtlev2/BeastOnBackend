package com.beaston.backend.DTO.plans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseDetailDTO {
    private Long exerciseId;
    private String exerciseName;
    private int weight;
    private int repetitions;
}


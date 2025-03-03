package com.beaston.backend.DTO.exercises;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignExerciseDto {
    private Long exerciseId;
    private Double weight;
    private Integer repetitions;
}

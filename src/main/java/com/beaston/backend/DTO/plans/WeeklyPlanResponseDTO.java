package com.beaston.backend.DTO.plans;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WeeklyPlanResponseDTO {
    private int dayOfWeek;
    private Long trainingPlanId;
    private String trainingPlanName;
    private List<ExerciseDetailDTO> exercises;
}

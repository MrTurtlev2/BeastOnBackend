package com.beaston.backend.DTO;

import lombok.Data;

import java.util.List;

@Data
public class WeeklyPlanResponseDTO {
    private Long trainingPlanId;
    private String trainingPlanName;
    private Integer dayOfWeek;
    private List<ExerciseDetailDTO> exercises;
}

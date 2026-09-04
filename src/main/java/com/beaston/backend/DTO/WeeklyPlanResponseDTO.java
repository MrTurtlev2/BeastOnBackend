package com.beaston.backend.DTO;

import lombok.Data;

import java.util.List;

@Data
public class WeeklyPlanResponseDTO {
    private String uuid;
    private String trainingPlanName;
    private List<Integer> daysOfWeek;
    private List<ExerciseDetailDTO> exercises;
}

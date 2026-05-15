package com.beaston.backend.DTO;

import lombok.Data;

import java.util.List;

@Data
public class TrainingPlanDTO {
    private String uuid;
    private String name;
    private List<Integer> daysOfWeek;
    private List<ExerciseDTO> exercises;
    private long lastModified;
}

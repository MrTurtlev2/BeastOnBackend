package com.beaston.backend.DTO;

import lombok.Data;

import java.util.List;

@Data
public class ExerciseDetailDTO {
    private String exerciseName;
    private List<ExerciseSetDTO> sets;
}

package com.beaston.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingPlanExerciseDTO {
    private String exerciseName;
    private Integer orderIndex;
    private List<ExerciseSetDTO> sets;
}

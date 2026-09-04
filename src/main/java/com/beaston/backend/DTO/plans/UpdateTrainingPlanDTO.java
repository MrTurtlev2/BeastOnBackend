package com.beaston.backend.DTO.plans;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class UpdateTrainingPlanDTO {

    private Long lastModified;

    private String name;

    private List<Integer> daysOfWeek;

    private List<ExerciseDetailDTO> exercises;
}

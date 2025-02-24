package com.beaston.backend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TrainingPlanResponseDTO {
    private Long id;
    private String name;
    private List<TrainingScheduleDTO> trainingSchedules;
}

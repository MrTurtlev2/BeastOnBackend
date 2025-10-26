package com.beaston.backend.DTO;

import lombok.Data;

import java.util.List;

@Data
public class ExerciseDTO {
    private String name;
    private Integer orderIndex;
    private List<ExerciseSetDTO> sets;
}

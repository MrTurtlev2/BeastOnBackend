package com.beaston.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseSetDTO {
    private Double weight;
    private Integer repetitions;
    private Integer setNumber;
}

package com.beaston.backend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TrainingPlanDTO {
    private String name;
    private List<Integer> daysOfWeek; // 1 (poniedziałek) do 7 (niedziela)
}

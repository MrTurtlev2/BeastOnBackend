package com.beaston.backend.services;

import com.beaston.backend.DTO.*;
import com.beaston.backend.entities.*;
import com.beaston.backend.repositories.CustomerRepository;
import com.beaston.backend.repositories.TrainingPlanRepository;
import com.beaston.backend.repositories.TrainingScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingPlanService {

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TrainingScheduleRepository trainingScheduleRepository;

    @Autowired
    private CustomerService customerService;

    @Transactional
    public TrainingPlan createPlan(TrainingPlanDTO dto) {
        Long customerId = customerService.getAuthenticatedCustomerId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        TrainingPlan plan = new TrainingPlan();
        plan.setCustomer(customer);
        plan.setName(dto.getName());

        if (dto.getDaysOfWeek() != null) {
            for (Integer day : dto.getDaysOfWeek()) {
                TrainingSchedule schedule = new TrainingSchedule();
                schedule.setTrainingPlan(plan);
                schedule.setDayOfWeek(day);
                plan.getTrainingSchedules().add(schedule);
            }
        }

        if (dto.getExercises() != null) {
            int orderIndex = 1;
            for (ExerciseDTO exDto : dto.getExercises()) {
                TrainingPlanExercise tpe = new TrainingPlanExercise();
                tpe.setTrainingPlan(plan);
                tpe.setExerciseName(exDto.getName());
                tpe.setOrderIndex(orderIndex++);

                if (exDto.getSets() != null) {
                    int setIndex = 1;
                    for (ExerciseSetDTO setDto : exDto.getSets()) {
                        ExerciseSet set = new ExerciseSet();
                        set.setTrainingPlanExercise(tpe);
                        set.setWeight(setDto.getWeight());
                        set.setRepetitions(setDto.getRepetitions());
                        set.setSetNumber(setIndex++);
                        tpe.getSets().add(set);
                    }
                }

                plan.getTrainingPlanExercises().add(tpe);
            }
        }

        return trainingPlanRepository.save(plan);
    }

    public List<WeeklyPlanResponseDTO> getWeeklySchedule(Long customerId) {
        List<TrainingPlan> plans = trainingPlanRepository.findByCustomerId(customerId);

        return plans.stream()
                .flatMap(plan -> plan.getTrainingSchedules().stream().map(schedule -> {
                    WeeklyPlanResponseDTO dto = new WeeklyPlanResponseDTO();
                    dto.setTrainingPlanId(plan.getId());
                    dto.setTrainingPlanName(plan.getName());
                    dto.setDayOfWeek(schedule.getDayOfWeek());

                    List<ExerciseDetailDTO> exerciseDetails = plan.getTrainingPlanExercises().stream().map(tpe -> {
                        ExerciseDetailDTO exDto = new ExerciseDetailDTO();
                        exDto.setExerciseName(tpe.getExerciseName());
                        exDto.setSets(tpe.getSets().stream().map(set -> {
                            ExerciseSetDTO setDto = new ExerciseSetDTO();
                            setDto.setWeight(set.getWeight());
                            setDto.setRepetitions(set.getRepetitions());
                            return setDto;
                        }).collect(Collectors.toList()));
                        return exDto;
                    }).collect(Collectors.toList());

                    dto.setExercises(exerciseDetails);
                    return dto;
                }))
                .collect(Collectors.toList());
    }

    @Transactional
    public TrainingPlan addExerciseToPlan(Long planId, ExerciseDTO dto) {
        TrainingPlan plan = trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        TrainingPlanExercise tpe = new TrainingPlanExercise();
        tpe.setTrainingPlan(plan);
        tpe.setExerciseName(dto.getName());

        if (dto.getOrderIndex() != null) {
            tpe.setOrderIndex(dto.getOrderIndex());
        } else {
            int nextOrder = plan.getTrainingPlanExercises().size() + 1;
            tpe.setOrderIndex(nextOrder);
        }

        if (dto.getSets() == null || dto.getSets().isEmpty()) {
            ExerciseSet defaultSet = new ExerciseSet();
            defaultSet.setTrainingPlanExercise(tpe);
            defaultSet.setWeight(1.0);
            defaultSet.setRepetitions(1);
            defaultSet.setSetNumber(1);
            tpe.getSets().add(defaultSet);
        } else {
            int setNumber = 1;
            for (ExerciseSetDTO setDto : dto.getSets()) {
                ExerciseSet set = new ExerciseSet();
                set.setTrainingPlanExercise(tpe);
                set.setWeight(setDto.getWeight());
                set.setRepetitions(setDto.getRepetitions());
                set.setSetNumber(setNumber++);
                tpe.getSets().add(set);
            }
        }

        plan.getTrainingPlanExercises().add(tpe);
        return trainingPlanRepository.save(plan);
    }
}

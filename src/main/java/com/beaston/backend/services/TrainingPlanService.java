package com.beaston.backend.services;

import com.beaston.backend.DTO.ExerciseDTO;
import com.beaston.backend.DTO.ExerciseSetDTO;
import com.beaston.backend.DTO.TrainingPlanDTO;
import com.beaston.backend.entities.*;
import com.beaston.backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingPlanService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private TrainingPlanExerciseRepository trainingPlanExerciseRepository;

    @Autowired
    private ExerciseSetRepository exerciseSetRepository;

    @Autowired
    private TrainingScheduleRepository trainingScheduleRepository;

    @Autowired
    private CustomerService customerService;

    // ----------------- PLANY -----------------
    @Transactional
    public TrainingPlan createPlan(TrainingPlanDTO dto) {
        Long customerId = customerService.getAuthenticatedCustomerId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        TrainingPlan plan = new TrainingPlan();
        plan.setCustomer(customer);
        plan.setName(dto.getName());

        plan = trainingPlanRepository.save(plan);

        // Harmonogram dni
        if (dto.getDaysOfWeek() != null) {
            for (Integer day : dto.getDaysOfWeek()) {
                TrainingSchedule schedule = new TrainingSchedule();
                schedule.setTrainingPlan(plan);
                schedule.setDayOfWeek(day);
                plan.getTrainingSchedules().add(schedule);
            }
        }

        // Ćwiczenia i serie
        if (dto.getExercises() != null) {
            int orderIndex = 1;
            for (ExerciseDTO exDto : dto.getExercises()) {
                TrainingPlanExercise tpe = new TrainingPlanExercise();
                tpe.setTrainingPlan(plan);
                tpe.setOrderIndex(orderIndex++);
                tpe.setExerciseName(exDto.getName());
                plan.getTrainingPlanExercises().add(tpe);

                // Serie
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
            }
        }

        return trainingPlanRepository.save(plan);
    }

    public List<TrainingPlan> getMyPlans() {
        Long customerId = customerService.getAuthenticatedCustomerId();
        return trainingPlanRepository.findByCustomerId(customerId);
    }

    @Transactional
    public void deletePlan(Long planId) {
        TrainingPlan plan = trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        trainingPlanRepository.delete(plan);
    }

    // ----------------- ĆWICZENIA -----------------
    @Transactional
    public TrainingPlanExercise addExercise(Long planId, ExerciseDTO dto) {
        TrainingPlan plan = trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        int orderIndex = plan.getTrainingPlanExercises().size() + 1;
        TrainingPlanExercise tpe = new TrainingPlanExercise();
        tpe.setTrainingPlan(plan);
        tpe.setOrderIndex(orderIndex);
        tpe.setExerciseName(dto.getName());
        plan.getTrainingPlanExercises().add(tpe);

        // Serie
        if (dto.getSets() != null) {
            int setIndex = 1;
            for (ExerciseSetDTO setDto : dto.getSets()) {
                ExerciseSet set = new ExerciseSet();
                set.setTrainingPlanExercise(tpe);
                set.setWeight(setDto.getWeight());
                set.setRepetitions(setDto.getRepetitions());
                set.setSetNumber(setIndex++);
                tpe.getSets().add(set);
            }
        }

        trainingPlanRepository.save(plan);
        return tpe;
    }

    @Transactional
    public void deleteExercise(Long exerciseId) {
        TrainingPlanExercise tpe = trainingPlanExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
        trainingPlanExerciseRepository.delete(tpe);
    }

    @Transactional
    public TrainingPlanExercise updateExercise(Long exerciseId, ExerciseDTO dto) {
        TrainingPlanExercise tpe = trainingPlanExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        tpe.setExerciseName(dto.getName());
        // opcjonalnie możesz też zaktualizować orderIndex, jeśli przesuwasz ćwiczenia
        return trainingPlanExerciseRepository.save(tpe);
    }

    // ----------------- SERIE -----------------
    @Transactional
    public ExerciseSet addSet(Long exerciseId, ExerciseSetDTO dto) {
        TrainingPlanExercise tpe = trainingPlanExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        int setNumber = tpe.getSets().size() + 1;
        ExerciseSet set = new ExerciseSet();
        set.setTrainingPlanExercise(tpe);
        set.setWeight(dto.getWeight());
        set.setRepetitions(dto.getRepetitions());
        set.setSetNumber(setNumber);

        tpe.getSets().add(set);
        trainingPlanExerciseRepository.save(tpe);

        return set;
    }

    @Transactional
    public void deleteSet(Long setId) {
        ExerciseSet set = exerciseSetRepository.findById(setId)
                .orElseThrow(() -> new RuntimeException("Set not found"));
        exerciseSetRepository.delete(set);
    }

    @Transactional
    public ExerciseSet updateSet(Long setId, ExerciseSetDTO dto) {
        ExerciseSet set = exerciseSetRepository.findById(setId)
                .orElseThrow(() -> new RuntimeException("Set not found"));

        set.setWeight(dto.getWeight());
        set.setRepetitions(dto.getRepetitions());
        return exerciseSetRepository.save(set);
    }
}

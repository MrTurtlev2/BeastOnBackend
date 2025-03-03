package com.beaston.backend.services;

import com.beaston.backend.DTO.TrainingPlanDTO;
import com.beaston.backend.DTO.TrainingPlanResponseDTO;
import com.beaston.backend.DTO.TrainingScheduleDTO;
import com.beaston.backend.entities.*;
import com.beaston.backend.repositories.CustomerExerciseRepository;
import com.beaston.backend.repositories.CustomerRepository;
import com.beaston.backend.repositories.TrainingPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingPlanService {
    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerExerciseRepository customerExerciseRepository;

    public TrainingPlan addTrainingPlan(Long customerId, TrainingPlanDTO trainingPlanDTO) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        TrainingPlan trainingPlan = new TrainingPlan();
        trainingPlan.setCustomer(customer);
        trainingPlan.setName(trainingPlanDTO.getName());

        // Save training plan
        TrainingPlan savedTrainingPlan = trainingPlanRepository.save(trainingPlan);

        // Add schedule for every day of week
        for (int day : trainingPlanDTO.getDaysOfWeek()) {
            TrainingSchedule trainingSchedule = new TrainingSchedule();
            trainingSchedule.setTrainingPlan(savedTrainingPlan);
            trainingSchedule.setDayOfWeek(day);

            savedTrainingPlan.getTrainingSchedules().add(trainingSchedule); // add schedule to list
        }

        trainingPlanRepository.save(savedTrainingPlan);

        return savedTrainingPlan;
    }

    public List<TrainingPlanResponseDTO> getTrainingPlansByCustomerId(Long customerId) {
        List<TrainingPlan> trainingPlans = trainingPlanRepository.findByCustomerId(customerId);
        return trainingPlans.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private TrainingPlanResponseDTO convertToDTO(TrainingPlan trainingPlan) {
        TrainingPlanResponseDTO response = new TrainingPlanResponseDTO();
        response.setId(trainingPlan.getId());
        response.setName(trainingPlan.getName());

        List<TrainingScheduleDTO> scheduleDTOs = trainingPlan.getTrainingSchedules().stream()
                .map(schedule -> {
                    TrainingScheduleDTO dto = new TrainingScheduleDTO();
                    dto.setId(schedule.getId());
                    dto.setDayOfWeek(schedule.getDayOfWeek());
                    return dto;
                }).collect(Collectors.toList());

        response.setTrainingSchedules(scheduleDTOs);
        return response;
    }


    public void assignExerciseToTrainingPlan(Long planId, Long exerciseId) {
        // Pobierz plan treningowy
        TrainingPlan trainingPlan = trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan treningowy nie znaleziony"));

        // Pobierz ćwiczenie przypisane do użytkownika
        CustomerExercise customerExercise = customerExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ćwiczenie użytkownika nie znalezione"));

        // Oblicz orderIndex na podstawie już przypisanych ćwiczeń do planu
        int orderIndex = trainingPlan.getTrainingPlanExercises().size() + 1; // Automatycznie inkrementuje orderIndex

        // Utwórz nowy obiekt TrainingPlanExercise
        TrainingPlanExercise trainingPlanExercise = new TrainingPlanExercise();
        trainingPlanExercise.setTrainingPlan(trainingPlan);
        trainingPlanExercise.setCustomerExercise(customerExercise);
        trainingPlanExercise.setOrderIndex(orderIndex);

        // Dodaj ćwiczenie do planu treningowego
        trainingPlan.getTrainingPlanExercises().add(trainingPlanExercise); // Dodaj do listy ćwiczeń w planie

        // Zaktualizuj plan treningowy
        trainingPlanRepository.save(trainingPlan);
    }


}



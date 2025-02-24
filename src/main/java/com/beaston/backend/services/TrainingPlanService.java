package com.beaston.backend.services;

import com.beaston.backend.DTO.TrainingPlanDTO;
import com.beaston.backend.DTO.TrainingPlanResponseDTO;
import com.beaston.backend.DTO.TrainingScheduleDTO;
import com.beaston.backend.entities.Customer;
import com.beaston.backend.entities.TrainingPlan;
import com.beaston.backend.entities.TrainingSchedule;
import com.beaston.backend.repositories.CustomerRepository;
import com.beaston.backend.repositories.TrainingPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingPlanService {
    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private CustomerRepository customerRepository;

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


}



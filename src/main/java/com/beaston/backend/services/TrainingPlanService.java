package com.beaston.backend.services;

import com.beaston.backend.DTO.TrainingPlanDTO;
import com.beaston.backend.DTO.TrainingPlanResponseDTO;
import com.beaston.backend.DTO.TrainingScheduleDTO;
import com.beaston.backend.DTO.plans.ExerciseDetailDTO;
import com.beaston.backend.DTO.plans.WeeklyPlanResponseDTO;
import com.beaston.backend.entities.Customer;
import com.beaston.backend.entities.TrainingPlan;
import com.beaston.backend.entities.TrainingSchedule;
import com.beaston.backend.repositories.CustomerExerciseRepository;
import com.beaston.backend.repositories.CustomerRepository;
import com.beaston.backend.repositories.ExerciseRepository;
import com.beaston.backend.repositories.TrainingPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingPlanService {

    private final CustomerService customerService;

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerExerciseRepository customerExerciseRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    public TrainingPlanService(CustomerService customerService) {
        this.customerService = customerService;
    }

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

    public List<WeeklyPlanResponseDTO> getWeeklySchedule(Long customerId) {
        List<TrainingPlan> trainingPlans = trainingPlanRepository.findByCustomerId(customerId);

        return trainingPlans.stream()
                .flatMap(plan -> plan.getTrainingSchedules().stream().map(schedule -> {
                    WeeklyPlanResponseDTO dto = new WeeklyPlanResponseDTO();
//                    dto.setDayOfWeek(schedule.getDayOfWeek());
                    dto.setDayOfWeek("day of week test");
                    dto.setTrainingPlanId(plan.getId());
                    dto.setTrainingPlanName(plan.getName());

                    List<ExerciseDetailDTO> exerciseDetails = plan.getTrainingPlanExercises().stream()
                            .map(tpe -> {
                                ExerciseDetailDTO exerciseDto = new ExerciseDetailDTO();
                                exerciseDto.setExerciseId(tpe.getCustomerExercise().getExercise().getId());
                                exerciseDto.setExerciseName(tpe.getCustomerExercise().getExercise().getName());
//                                exerciseDto.setWeight(tpe.getCustomerExercise().getWeight());
                                exerciseDto.setWeight(20);
                                exerciseDto.setRepetitions(tpe.getCustomerExercise().getRepetitions());
                                return exerciseDto;
                            })
                            .collect(Collectors.toList());

                    dto.setExercises(exerciseDetails);
                    return dto;
                }))
                .collect(Collectors.toList());
    }


    public void assignExerciseToTrainingPlan(Long planId, Long exerciseId, Long customerId) {

//        // Pobierz plan treningowy
//        TrainingPlan trainingPlan = trainingPlanRepository.findById(planId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan treningowy nie znaleziony"));
//
//        // Pobierz globalne ćwiczenie
//        Exercise exercise = exerciseRepository.findById(exerciseId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ćwiczenie nie znalezione"));
//
//        // Stwórz CustomerExercise dla użytkownika
//        CustomerExercise customerExercise1 = new CustomerExercise();
//        customerExercise1.setCustomerId(customerId);
//        customerExercise.setExercise(exercise);
//        customerExercise.setWeight(0.0); // Domyślna wartość
//        customerExercise.setRepetitions(0); // Domyślna wartość
//        customerExercise = userExerciseRepository.save(customerExercise); // Zapisujemy ćwiczenie użytkownika
//
//        // Oblicz orderIndex na podstawie ilości przypisanych ćwiczeń
//        int orderIndex = trainingPlan.getTrainingPlanExercises().size() + 1;
//
//        // Stwórz nowe powiązanie ćwiczenia z planem
//        TrainingPlanExercise trainingPlanExercise = new TrainingPlanExercise();
//        trainingPlanExercise.setTrainingPlan(trainingPlan);
//        trainingPlanExercise.setCustomerExercise(customerExercise);
//        trainingPlanExercise.setOrderIndex(orderIndex);
//
//        // Dodaj powiązanie do planu i zapisz
//        trainingPlan.getTrainingPlanExercises().add(trainingPlanExercise);
//        trainingPlanRepository.save(trainingPlan);
    }


}



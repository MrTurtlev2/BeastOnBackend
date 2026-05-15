package com.beaston.backend.repositories;

import com.beaston.backend.entities.Customer;
import com.beaston.backend.entities.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
    List<TrainingPlan> findByCustomer(Customer customer);

    List<TrainingPlan> findByCustomerId(Long customerId);
    
    Optional<TrainingPlan> findByUuid(String uuid);
}


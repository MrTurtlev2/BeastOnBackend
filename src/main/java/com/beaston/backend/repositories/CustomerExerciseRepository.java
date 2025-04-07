package com.beaston.backend.repositories;

import com.beaston.backend.entities.Customer;
import com.beaston.backend.entities.CustomerExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerExerciseRepository extends JpaRepository<CustomerExercise, Long> {
    List<CustomerExercise> findByCustomer(Customer customer);
}


package com.beaston.backend.repositories;

import com.beaston.backend.entities.User;
import com.beaston.backend.entities.UserExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Long> {
    List<UserExercise> findByUser(User user);
}


package com.beaston.backend.entities;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "training_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "training_plan_id", nullable = false)
    private TrainingPlan trainingPlan;

    @Column(nullable = false)
    private Integer dayOfWeek; // 1 = monday, 7 = Sunday
}


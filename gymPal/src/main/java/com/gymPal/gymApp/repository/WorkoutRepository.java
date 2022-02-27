package com.gymPal.gymApp.repository;

import com.gymPal.gymApp.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout,Long> {

    Workout findByWorkoutName(String email);
}

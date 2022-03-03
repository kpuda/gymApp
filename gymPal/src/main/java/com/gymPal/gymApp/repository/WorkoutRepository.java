package com.gymPal.gymApp.repository;

import com.gymPal.gymApp.entity.Workout;
import com.gymPal.gymApp.model.WorkoutModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout,Long> {

    Workout findByWorkoutName(String email);

    Optional<List<WorkoutModel>> getByWorkoutOwner(long email);
}

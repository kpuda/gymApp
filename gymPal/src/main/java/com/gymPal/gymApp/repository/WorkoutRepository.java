package com.gymPal.gymApp.repository;

import com.gymPal.gymApp.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout,Long> {

    Workout findByWorkoutName(String workoutName);

    List<Workout> findAllByWorkoutOwner(long id);

    void deleteByWorkoutName(String workoutName);
}

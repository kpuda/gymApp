package com.gymPal.gymApp.service;

import com.gymPal.gymApp.model.NewWorkoutModel;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface WorkoutService {
    ResponseEntity<?> getWorkout(String email);

    ResponseEntity saveNewWorkout(NewWorkoutModel workout, Principal principal);

    ResponseEntity getWorkouts(String name);

    ResponseEntity removeWorkout(String workoutName, Principal principal);

    ResponseEntity updateWorkout(NewWorkoutModel workout, Principal principal);
}

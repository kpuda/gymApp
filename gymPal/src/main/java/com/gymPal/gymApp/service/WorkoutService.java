package com.gymPal.gymApp.service;

import com.gymPal.gymApp.entity.Workout;
import com.gymPal.gymApp.model.NewWorkoutModel;

public interface WorkoutService {
    Workout getWorkout(String email);

    String saveNewWorkout(NewWorkoutModel workout);
}

package com.gymPal.gymApp.controller;

import com.gymPal.gymApp.entity.Workout;
import com.gymPal.gymApp.model.NewWorkoutModel;
import com.gymPal.gymApp.service.WorkoutService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workout")
@AllArgsConstructor
public class WorkoutController {

    WorkoutService workoutService;
    @GetMapping("/getWorkout")
    public Workout getWorkout(){
        return workoutService.getWorkout("kp@gmail.com");
    }

    @PostMapping("/saveWorkout")
    public String saveWorkout(@RequestBody NewWorkoutModel workout){
        return workoutService.saveNewWorkout(workout);
    }
}

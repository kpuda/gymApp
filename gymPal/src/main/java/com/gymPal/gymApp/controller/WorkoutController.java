package com.gymPal.gymApp.controller;

import com.gymPal.gymApp.model.NewWorkoutModel;
import com.gymPal.gymApp.service.WorkoutService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/workout")// TODO add /api
@AllArgsConstructor
public class WorkoutController {

    WorkoutService workoutService;
    @GetMapping("/getWorkout")
    public ResponseEntity<?> getWorkout(@RequestParam("workoutName") String workoutName){
        return workoutService.getWorkout(workoutName);
    }

    @GetMapping("/getWorkoutsByUsername")
    public ResponseEntity<?> getWorkouts(Principal principal){
        return workoutService.getWorkouts(principal.getName());
    }

    @PostMapping("/saveWorkout")
    public ResponseEntity<?> saveWorkout(@RequestBody NewWorkoutModel workout,Principal principal){
        return workoutService.saveNewWorkout(workout,principal);
    }

    @PutMapping("/updateWorkout")
    public ResponseEntity<?> updateWorkout(@RequestBody NewWorkoutModel workout, Principal principal){
        return workoutService.updateWorkout(workout,principal);
    }

    @DeleteMapping("/removeWorkout")
    public ResponseEntity<?> removeWorkout(@RequestParam("workoutName") String workoutName,Principal principal){
        return workoutService.removeWorkout(workoutName,principal);
    }

}

package com.gymPal.gymApp.service.impl;

import com.gymPal.gymApp.entity.Workout;
import com.gymPal.gymApp.model.NewWorkoutModel;
import com.gymPal.gymApp.repository.ExcerciseRepository;
import com.gymPal.gymApp.repository.UserRepository;
import com.gymPal.gymApp.repository.WorkoutRepository;
import com.gymPal.gymApp.service.WorkoutService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.gymPal.gymApp.utils.ServerConsts.*;

@Service
@AllArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {

    WorkoutRepository workoutRepository;
    ExcerciseRepository excerciseRepository;
    UserRepository userRepository;

    @Override
    public ResponseEntity<?> getWorkout(String workoutName) {
        Workout workout = workoutRepository.findByWorkoutName(workoutName);
        return ResponseEntity.ok().body(workout);
    }

    @Override
    public ResponseEntity<?> getWorkouts(String name) {
        long userId = userRepository.findByUsername(name).getId();
        List<Workout> workoutList = workoutRepository.findAllByWorkoutOwner(userId);
        return ResponseEntity.ok().body(workoutList);
    }

    @Override
    public ResponseEntity<?> saveNewWorkout(NewWorkoutModel workoutModel, Principal principal) {
        long userId = userRepository.findByUsername(principal.getName()).getId();
        if (userId == 0) {
            return ResponseEntity.status(404).body(USER_NOT_FOUND);
        } else {
            Workout workout = mapWorkoutModelToWorkout(workoutModel, userId);
            workoutRepository.save(workout);
        }
        return ResponseEntity.ok().body(WORKOUT_SAVED);
    }

    @Override
    public ResponseEntity<?> updateWorkout(NewWorkoutModel updatedWorkout, Principal principal) {
        Optional<Workout> workoutOptional = workoutRepository.findById(updatedWorkout.getWorkoutId());
        if (workoutOptional.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            Workout workout = mapUpdatedWorkout(workoutOptional, updatedWorkout);
            workoutRepository.save(workout);
        }
        return ResponseEntity.ok().body(WORKOUT_UPDATED);
    }


    @Override
    public ResponseEntity<?> removeWorkout(String workoutName, Principal principal) {
        long userId = userRepository.findByUsername(principal.getName()).getId();
        List<Workout> allByWorkoutOwner = workoutRepository.findAllByWorkoutOwner(userId);
        Optional<Workout> first = allByWorkoutOwner.stream().filter(workout -> workout.getWorkoutName().equals(workoutName)).findFirst();
        if (first.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else
            workoutRepository.deleteByWorkoutName(workoutName);
        return ResponseEntity.ok().body("WORKOUT_DELETED");
    }

    private Workout mapWorkoutModelToWorkout(NewWorkoutModel newWorkoutModel, long userId) {
        Workout workout = new Workout();
        Set<Long> userSet = new HashSet<>();
        userSet.add(userId);
        workout.setWorkoutName(newWorkoutModel.getWorkoutName());
        workout.setWorkoutOwner(userId);
        workout.setPublic(newWorkoutModel.isWorkoutPublic());
        workout.setExcerciseList(newWorkoutModel.getExcerciseList());
        workout.setUserSet(userSet);

        return workout;
    }

    private Workout mapUpdatedWorkout(Optional<Workout> workoutOptional, NewWorkoutModel updatedWorkout) {
        Workout workout = workoutOptional.get();
        if (!workout.getWorkoutName().equals(updatedWorkout.getWorkoutName())) {
            workout.setWorkoutName(updatedWorkout.getWorkoutName());
        }
        if (workout.isPublic() != updatedWorkout.isWorkoutPublic()) {
            workout.setPublic(updatedWorkout.isWorkoutPublic());
        }
        if (!workout.getExcerciseList().equals(updatedWorkout.getExcerciseList())) {
            workout.setExcerciseList(updatedWorkout.getExcerciseList());
        }
        return workout;
    }

}

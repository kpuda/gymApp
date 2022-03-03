package com.gymPal.gymApp.service.impl;

import com.gymPal.gymApp.entity.User;
import com.gymPal.gymApp.entity.Workout;
import com.gymPal.gymApp.model.ExcerciseRepsAndWeight;
import com.gymPal.gymApp.model.NewWorkoutModel;
import com.gymPal.gymApp.model.WorkoutModel;
import com.gymPal.gymApp.repository.ExcerciseRepository;
import com.gymPal.gymApp.repository.UserRepository;
import com.gymPal.gymApp.repository.WorkoutRepository;
import com.gymPal.gymApp.service.WorkoutService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {

    WorkoutRepository workoutRepository;
    ExcerciseRepository excerciseRepository;
    UserRepository userRepository;

    @Override
    public Workout getWorkout(String workoutName) {

        Workout workout = workoutRepository.findByWorkoutName(workoutName);
        return workout;
    }

    private Workout mapWorkoutModelToWorkout(NewWorkoutModel newWorkoutModel, User user) {
        Workout workout = new Workout();
        WorkoutModel workoutModel = new WorkoutModel();
        Set<User> userSet = new HashSet<>();
        Map<Long, ExcerciseRepsAndWeight> excerciseList = newWorkoutModel.getExcerciseMap();
        workoutModel.setExcerciseMap(excerciseList);
        userSet.add(user);
        workout.setWorkoutName(newWorkoutModel.getWorkoutName());
        workout.setWorkoutOwner(user.getId());
        workout.setPublic(newWorkoutModel.isWorkoutPublic());
        workout.setExcerciseMap(excerciseList);
        workout.setUserSet(userSet);

        return workout;
    }

    @Override
    public String saveNewWorkout(NewWorkoutModel workoutModel) {
        User user = userRepository.findByEmail(workoutModel.getEmail());
        if (user == null) {
            return "no"; //TODO
        } else {
            Workout workout = mapWorkoutModelToWorkout(workoutModel, user);

            workoutRepository.save(workout);
        }
        return "yes";
    }
}

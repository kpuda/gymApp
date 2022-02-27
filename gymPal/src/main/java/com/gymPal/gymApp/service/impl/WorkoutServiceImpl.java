package com.gymPal.gymApp.service.impl;

import com.gymPal.gymApp.entity.Excercise;
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

import java.util.*;

@Service
@AllArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {

    WorkoutRepository workoutRepository;
    ExcerciseRepository excerciseRepository;
    UserRepository userRepository;

    @Override
    public Workout getWorkout(String workoutName) {
        /*User user = userRepository.findByEmail(workoutName);
        Workout workout = new Workout();
        Set<User> userSet = new HashSet<>();
        userSet.add(user);
        List<Excercise> excercises = new ArrayList<>();
        excercises.add(new Excercise("Bench press", BodyPart.CHEST, ExcerciseIntesitivity.HIGH, ExcerciseRank.MAIN, WeightType.ATLAS));
        excercises.add(new Excercise("Cable curl", BodyPart.BICEPS, ExcerciseIntesitivity.HIGH, ExcerciseRank.MAIN, WeightType.ATLAS));
        workout.setUserSet(userSet);
        workout.setExcercises(excercises);
        workoutRepository.save(workout);*/
        Workout workout= workoutRepository.findByWorkoutName(workoutName);
        return workout;
    }

    @Override
    public String saveNewWorkout(NewWorkoutModel workoutModel) {
        User user = userRepository.findByEmail(workoutModel.getEmail());
        if (user == null) {
            return null; //TODO
        } else {
            Workout workout = mapWorkoutModelToWorkout(workoutModel);

            workoutRepository.save(workout);
        }
        return "saved";
    }

    private Workout mapWorkoutModelToWorkout(NewWorkoutModel newWorkoutModel) {
        Workout workout = new Workout();
        WorkoutModel workoutModel= new WorkoutModel();
        Set<User> userSet = new HashSet<>();
        List<Excercise> excercises = new ArrayList<>();
        List<WorkoutModel> workoutModelList= new ArrayList<>();

        Map<Excercise, ExcerciseRepsAndWeight> excerciseList = newWorkoutModel.getExcerciseList();
        //workoutModel.setExcerciseMap(newWorkoutModel.getExcerciseList());
        workoutModelList.add(workoutModel);

        workout.setWorkoutName(newWorkoutModel.getWorkoutName());

       // workout.setWorkoutModels(workoutModelList);
        workout.setUserSet(userSet);
       // workout.setExcercises(excercises);

        return workout;
    }
}

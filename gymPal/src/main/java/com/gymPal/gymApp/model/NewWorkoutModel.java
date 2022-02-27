package com.gymPal.gymApp.model;

import com.gymPal.gymApp.entity.Excercise;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class NewWorkoutModel {

    private String workoutName;
    private String email;
    private Map<Excercise,ExcerciseRepsAndWeight> excerciseList;
}

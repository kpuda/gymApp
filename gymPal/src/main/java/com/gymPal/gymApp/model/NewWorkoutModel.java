package com.gymPal.gymApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NewWorkoutModel {

    private long workoutId;
    private String workoutName;
    private String email;
    private boolean isWorkoutPublic=false;
    private List<ExcerciseRepsAndWeight> excerciseList;
}

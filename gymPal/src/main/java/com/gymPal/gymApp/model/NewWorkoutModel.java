package com.gymPal.gymApp.model;

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
    private boolean isWorkoutPublic=false;
    private Map<Long,ExcerciseRepsAndWeight> excerciseMap;
}

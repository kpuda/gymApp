package com.gymPal.gymApp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NewWorkoutModel {

    private String email;
    private List<NewWorkoutExcerciseModel> excerciseList;
}

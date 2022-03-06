package com.gymPal.gymApp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.sql.Time;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ExcerciseRepsAndWeight {

    private String excerciseName;
    private int sets;
    private int reps;
    private double weight;
    private Time rest;
}

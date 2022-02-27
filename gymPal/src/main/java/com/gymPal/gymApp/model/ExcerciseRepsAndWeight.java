package com.gymPal.gymApp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
@Setter
@Data
@AllArgsConstructor
@Embeddable
public class ExcerciseRepsAndWeight {

    private int sets;
    private int reps;
    private double weight;
}

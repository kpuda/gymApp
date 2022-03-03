package com.gymPal.gymApp.model;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ExcerciseRepsAndWeight {

    private int sets;
    private int reps;
    private double weight;
}

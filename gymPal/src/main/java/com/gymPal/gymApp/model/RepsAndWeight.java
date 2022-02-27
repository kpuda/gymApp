package com.gymPal.gymApp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
@Setter
@Data
@Embeddable
public class RepsAndWeight {

    private int reps;
    private double weight;
}

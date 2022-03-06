package com.gymPal.gymApp.controller.testing;

import com.gymPal.gymApp.model.ExcerciseModel;
import com.gymPal.gymApp.service.ExcerciseService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/excercise")
@AllArgsConstructor
public class ExcercisesController {

    ExcerciseService excerciseService;

    @PostMapping("/addExcercise")
    public String addExcercise(@RequestBody ExcerciseModel excerciseModel) {
        return excerciseService.addExcercise(excerciseModel);
    }

    @GetMapping("/generateExcercises")
    public String generate() {
        return excerciseService.generatePayload();
    }

    @GetMapping("/getAllExcercises")
    public ResponseEntity getAllExcercises() {
        return excerciseService.getAllExcercices();
    }


}

package ru.nstu.exam.bean;

import lombok.Data;

import java.util.List;

@Data
public class CreateRatingSystemBean {

    private String name;

    private List<Integer> approvedRatingsForQuestion;
    private List<Integer> rejectedRatingsForQuestion;

    private List<Integer> approvedRatingsForExercise;
    private List<Integer> rejectedRatingsForExercise;

}

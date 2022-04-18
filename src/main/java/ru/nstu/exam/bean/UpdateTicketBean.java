package ru.nstu.exam.bean;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateTicketBean {

    @NotNull
    private Long id;

    @NotNull
    private Integer semesterRating;

    @NotNull
    private Integer examRating;

    @NotNull
    private Boolean allowed;
}

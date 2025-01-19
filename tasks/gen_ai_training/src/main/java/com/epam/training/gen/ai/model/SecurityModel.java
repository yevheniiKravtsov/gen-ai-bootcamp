package com.epam.training.gen.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class SecurityModel {
    private int id;
    private String location;
    private boolean isOn;
}

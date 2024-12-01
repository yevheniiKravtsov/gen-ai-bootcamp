package com.epam.training.gen.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class GenAiResponse {
    private List<String> response;
}

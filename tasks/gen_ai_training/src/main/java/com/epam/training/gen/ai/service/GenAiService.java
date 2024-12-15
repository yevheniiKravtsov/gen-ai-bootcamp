package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.model.GenAiResponse;
import org.springframework.stereotype.Service;

@Service
public interface GenAiService {
    GenAiResponse getGeneratedResponse(String prompt);

    GenAiResponse processWithHistory(String prompt);
}

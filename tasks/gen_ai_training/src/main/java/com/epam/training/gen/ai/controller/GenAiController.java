package com.epam.training.gen.ai.controller;


import com.epam.training.gen.ai.model.GenAiResponse;
import com.epam.training.gen.ai.model.Prompt;
import com.epam.training.gen.ai.service.GenAiService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@AllArgsConstructor
@RestController
public class GenAiController {

    private final GenAiService genAiService;

    @PostMapping("/gen-ai/basic")
    public ResponseEntity<GenAiResponse> getResponse(@RequestBody Prompt prompt) {
        if (prompt == null || prompt.getInput().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(genAiService.getGeneratedResponse(prompt.getInput()));
    }

    @PostMapping("/gen-ai/context")
    public ResponseEntity<GenAiResponse> getResponseWithHistory(@RequestBody Prompt prompt) {
        if (prompt == null || prompt.getInput().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        GenAiResponse genAiResponse = genAiService.processWithHistory(prompt.getInput());
        if (Objects.isNull(genAiResponse)) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(genAiResponse);
    }
}

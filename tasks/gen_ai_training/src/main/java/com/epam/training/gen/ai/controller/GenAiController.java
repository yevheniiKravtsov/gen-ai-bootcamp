package com.epam.training.gen.ai.controller;


import com.epam.training.gen.ai.model.GenAiResponse;
import com.epam.training.gen.ai.model.Prompt;
import com.epam.training.gen.ai.service.GenAiService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
public class GenAiController {

    private final GenAiService genAiService;

    @PostMapping("/gen-ai")
    public ResponseEntity<GenAiResponse> getResponse(@RequestBody Prompt prompt) {
        if (prompt == null || prompt.getInput().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(genAiService.getGeneratedResponse(prompt.getInput()));
    }
}

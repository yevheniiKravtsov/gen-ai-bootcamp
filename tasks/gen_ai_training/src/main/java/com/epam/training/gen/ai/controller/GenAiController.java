package com.epam.training.gen.ai.controller;


import com.azure.ai.openai.models.EmbeddingItem;
import com.epam.training.gen.ai.model.GenAiResponse;
import com.epam.training.gen.ai.model.Prompt;
import com.epam.training.gen.ai.service.GenAiService;
import com.epam.training.gen.ai.service.impl.VectorService;
import io.qdrant.client.grpc.Points;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@RestController
public class GenAiController {

    private final GenAiService genAiService;
    private final VectorService vectorService;

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

    @SneakyThrows
    @PostMapping("/gen-ai/embedding/save")
    public void createAndSaveEmbedding(@RequestBody Prompt request) {
        vectorService.processAndSaveText(request.getInput());
    }

    @SneakyThrows
    @PostMapping("/gen-ai/embedding")
    public List<EmbeddingItem> createEmbedding(@RequestBody Prompt request) {
        return vectorService.getEmbeddings(request.getInput());
    }

    @SneakyThrows
    @PostMapping("/gen-ai/embedding/search")
    public String getEmbedding(@RequestBody Prompt request) {
        return vectorService.search(request.getInput()).stream()
                .map(scoredPoint -> scoredPoint.getPayloadMap().get("info").getStringValue())
                .findFirst()
                .get();
    }
}

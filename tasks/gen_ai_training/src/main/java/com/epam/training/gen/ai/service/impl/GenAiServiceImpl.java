package com.epam.training.gen.ai.service.impl;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.epam.training.gen.ai.model.GenAiResponse;
import com.epam.training.gen.ai.service.GenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenAiServiceImpl implements GenAiService {

    @Value("${client-openai-deployment-name}")
    private String deployment;

    private final OpenAIAsyncClient aiAsyncClient;

    @Override
    public GenAiResponse getGeneratedResponse(String prompt) {
        var completions = aiAsyncClient
                .getChatCompletions(
                        deployment,
                        new ChatCompletionsOptions(
                                List.of(new ChatRequestUserMessage(prompt))))
                .block();
        var messages = Objects.requireNonNull(completions).getChoices().stream()
                .map(c -> c.getMessage().getContent())
                .toList();
        log.info(messages.toString());
        return new GenAiResponse(messages);
    }
}

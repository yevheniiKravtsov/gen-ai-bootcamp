package com.epam.training.gen.ai.service.impl;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.epam.training.gen.ai.model.GenAiResponse;
import com.epam.training.gen.ai.service.GenAiService;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
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
    private static final String AI_ANSWER = "AI answer: {}";
    private static final String GEN_AI_RESPONSE_IS_NULL = "Gen AI response is null";
    private static final String REQUEST = "request";
    private static final String CHAT_HISTORY = "chatHistory";
    private static final String PROMPT = """
            {{$chatHistory}}
            <message role="user">{{$request}}</message>""";


    private final OpenAIAsyncClient aiAsyncClient;

    private final Kernel kernel;

    private final InvocationContext invocationContext;

    private final ChatHistory chatHistory;

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

    @Override
    public GenAiResponse processWithHistory(String prompt) {

        var response = kernel.invokeAsync(getChat())
                .withInvocationContext(invocationContext)
                .withArguments(getKernelFunctionArguments(prompt, chatHistory))
                .block();

        chatHistory.addUserMessage(prompt);

        if (Objects.nonNull(response) && Objects.nonNull(response.getResult())) {
            chatHistory.addAssistantMessage(response.getResult());
            log.info(AI_ANSWER, response.getResult());
            return new GenAiResponse(List.of(response.getResult()));
        } else {
            return new GenAiResponse(List.of(GEN_AI_RESPONSE_IS_NULL));
        }
    }

    private KernelFunction<String> getChat() {
        return KernelFunction.<String>createFromPrompt(PROMPT)
                .build();
    }

    private KernelFunctionArguments getKernelFunctionArguments(String prompt, ChatHistory chatHistory) {
        return KernelFunctionArguments.builder()
                .withVariable(REQUEST, prompt)
                .withVariable(CHAT_HISTORY, chatHistory)
                .build();
    }
}

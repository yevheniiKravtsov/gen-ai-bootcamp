package com.epam.training.gen.ai.config;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    @Value("${client-openai-key}")
    private String apiKey;

    @Value("${client-openai-endpoint}")
    private String endpoint;

    @Value("${client-openai-deployment-name}")
    private String deploymentOrModelName;

    private final DeploymentProperties deploymentProperties;

    @Bean
    public OpenAIAsyncClient openAIAsyncClient() {
        return new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(apiKey))
                .endpoint(endpoint)
                .buildAsyncClient();
    }

    @Bean
    public ChatCompletionService chatCompletionService(OpenAIAsyncClient openAIAsyncClient) {
        return OpenAIChatCompletion.builder()
                .withModelId(deploymentOrModelName)
                .withOpenAIAsyncClient(openAIAsyncClient)
                .build();
    }

    @Bean
    public InvocationContext invocationContext(Map<String, PromptExecutionSettings> promptExecutionsSettingsMap) {
        return InvocationContext.builder()
                .withPromptExecutionSettings(promptExecutionsSettingsMap.get(deploymentOrModelName))
                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                .build();
    }

    @Bean
    @Scope(scopeName = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public ChatHistory chatHistory() {
        return new ChatHistory();
    }

    @Bean
    public Map<String, PromptExecutionSettings> promptExecutionsSettingsMap() {
        return deploymentProperties.getDeployments().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue,
                        e -> PromptExecutionSettings.builder()
                                .withTemperature(1.0)
                                .build()));
    }

    @Bean
    public QdrantClient qdrantClient() {
        return new QdrantClient(QdrantGrpcClient.newBuilder(
                "localhost", 6334, false).build());
    }
}

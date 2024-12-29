package com.epam.training.gen.ai.config;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.epam.training.gen.ai.plugin.SimplePlugin;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.InvocationReturnMode;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
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

    /**
     * Creates a {@link KernelPlugin} bean using a simple plugin.
     *
     * @return an instance of {@link KernelPlugin}
     */
    @Bean
    public KernelPlugin kernelPlugin() {
        return KernelPluginFactory.createFromObject(
                new SimplePlugin(), "Simple Plugin");
    }

    @Bean
    public Kernel kernel(ChatCompletionService chatCompletionService, KernelPlugin kernelPlugin) {
        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .withPlugin(kernelPlugin)
                .build();
    }

    @Bean
    public InvocationContext invocationContext(Map<String, PromptExecutionSettings> promptExecutionsSettingsMap) {
        return InvocationContext.builder()
                .withPromptExecutionSettings(promptExecutionsSettingsMap.get(deploymentOrModelName))
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
}

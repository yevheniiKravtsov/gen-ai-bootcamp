package com.epam.training.gen.ai.config;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.epam.training.gen.ai.plugin.SecurityPlugin;
import com.epam.training.gen.ai.plugin.SimplePlugin;
import com.epam.training.gen.ai.plugin.TemperaturePlugin;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.InvocationReturnMode;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
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

    private static final String SIMPLE_PLUGIN = "SimplePlugin";
    private static final String SECURITY_PLUGIN = "SecurityPlugin";
    private static final String TEMPERATURE_PLUGIN = "TemperaturePlugin";
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
    public KernelPlugin createSimplePlugin() {
        return KernelPluginFactory.createFromObject(
                new SimplePlugin(), SIMPLE_PLUGIN);
    }

    public KernelPlugin createSecurityPlugin() {
        return KernelPluginFactory.createFromObject(
                new SecurityPlugin(), SECURITY_PLUGIN);
    }

    public KernelPlugin createTemperaturePlugin() {
        return KernelPluginFactory.createFromObject(
                new TemperaturePlugin(), TEMPERATURE_PLUGIN);
    }

    @Bean
    public Kernel kernel(ChatCompletionService chatCompletionService) {
        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .withPlugin(createSimplePlugin())
                .withPlugin(createSecurityPlugin())
                .withPlugin(createTemperaturePlugin())
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
}

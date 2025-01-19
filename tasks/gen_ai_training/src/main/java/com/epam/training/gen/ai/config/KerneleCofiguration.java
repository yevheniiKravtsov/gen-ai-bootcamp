package com.epam.training.gen.ai.config;

import com.epam.training.gen.ai.plugin.ElectronicComponentsPlugin;
import com.epam.training.gen.ai.plugin.SecurityPlugin;
import com.epam.training.gen.ai.plugin.SimplePlugin;
import com.epam.training.gen.ai.plugin.TemperaturePlugin;
import com.epam.training.gen.ai.service.impl.VectorService;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KerneleCofiguration {

    private static final String SIMPLE_PLUGIN = "SimplePlugin";
    private static final String SECURITY_PLUGIN = "SecurityPlugin";
    private static final String TEMPERATURE_PLUGIN = "TemperaturePlugin";
    private static final String COMPONENT_PLUGIN = "ElectronicComponentsPlugin";

    private final VectorService vectorService;

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

    public KernelPlugin createComponentPlugin() {
        return KernelPluginFactory.createFromObject(
                new ElectronicComponentsPlugin(vectorService), COMPONENT_PLUGIN);
    }

    @Bean
    public Kernel kernel(ChatCompletionService chatCompletionService) {
        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .withPlugin(createSimplePlugin())
                .withPlugin(createSecurityPlugin())
                .withPlugin(createTemperaturePlugin())
                .withPlugin(createComponentPlugin())
                .build();
    }
}

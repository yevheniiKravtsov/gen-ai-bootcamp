package com.epam.training.gen.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "deployments.azureopenai")
public class DeploymentProperties {
    private Map<String, String> deployments = new HashMap<>();
}

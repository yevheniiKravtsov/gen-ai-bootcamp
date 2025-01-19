package com.epam.training.gen.ai.plugin;

import com.epam.training.gen.ai.service.impl.VectorService;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ElectronicComponentsPlugin {
    private final VectorService vectorService;

    @SneakyThrows
    @DefineKernelFunction(name = "get_component",
            description = "Search for a information similar to the given query.")
    public String search(@KernelFunctionParameter(description = "Data on which to do action", name = "query") String query) {
        var points = vectorService.search(query);
        log.info("Points {}", points);
        return points.stream()
                .map(scoredPoint ->
                        scoredPoint.getPayloadMap().get("info").getStringValue()).findFirst().get();
    }
}

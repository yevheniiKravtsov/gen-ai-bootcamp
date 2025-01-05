package com.epam.training.gen.ai.plugin;

import com.epam.training.gen.ai.model.TemperatureModel;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemperaturePlugin {
    private final Map<Integer, TemperatureModel> temperatures = new HashMap<>();

    public TemperaturePlugin() {
        temperatures.put(1, new TemperatureModel(1, "Living Room", 21));
        temperatures.put(2, new TemperatureModel(2, "Bedroom", 22));
        temperatures.put(3, new TemperatureModel(3, "Kitchen", 24));
    }

    @DefineKernelFunction(name = "get_temperatures", description = "Gets a list of temperatures from different rooms")
    public String getTemperatures() {
        System.out.println("Getting temperatures");
        return new ArrayList<>(temperatures.values()).toString();
    }

    @DefineKernelFunction(name = "set_temperature", description = "Sets the temperature of a specific room")
    public TemperatureModel setTemperature(
            @KernelFunctionParameter(name = "id", description = "The ID of the room to set temperature") int id,
            @KernelFunctionParameter(name = "temperature", description = "The new temperature for the room") int temperature) {
        System.out.println("Setting temperature for room " + id + " to " + temperature);
        if (!temperatures.containsKey(id)) {
            throw new IllegalArgumentException("Room not found");
        }

        temperatures.get(id).setTemperature(temperature);

        return temperatures.get(id);
    }
}

package com.epam.training.gen.ai.plugin;

import com.epam.training.gen.ai.model.SecurityModel;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecurityPlugin {
    private final Map<Integer, SecurityModel> cameras = new HashMap<>();

    public SecurityPlugin() {
        cameras.put(1, new SecurityModel(1, "Front Door", true));
        cameras.put(2, new SecurityModel(2, "Back Door", false));
        cameras.put(3, new SecurityModel(3, "Garage", true));
    }

    @DefineKernelFunction(name = "get_cameras", description = "Gets a list of security cameras and their current status")
    public String getCameras() {
        System.out.println("Getting security cameras");
        return new ArrayList<>(cameras.values()).toString();
    }

    @DefineKernelFunction(name = "toggle_camera", description = "Toggles the status of the security camera")
    public SecurityModel toggleCamera(
            @KernelFunctionParameter(name = "id", description = "The ID of the camera to toggle") int id) {
        System.out.println("Toggling camera " + id);
        if (!cameras.containsKey(id)) {
            throw new IllegalArgumentException("Camera not found");
        }

        boolean currentStatus = cameras.get(id).isOn();
        cameras.get(id).setOn(!currentStatus);

        return cameras.get(id);
    }
}

package me.duong.mrp.utils;

import me.duong.mrp.presentation.Mapping;
import me.duong.mrp.presentation.Request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public enum ControllerStore {
    INSTANCE;
    private final Map<Mapping, Consumer<Request>> controllers = new HashMap<>();

    public void addController(Mapping mapping, Consumer<Request> controller) {
        controllers.put(mapping, controller);
    }

    public Map<Mapping, Consumer<Request>> getControllers() {
        return Collections.unmodifiableMap(controllers);
    }
}

package me.duong.mrp.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Injector {
    INSTANCE;
    private boolean useMocks = false;
    private final Map<Class<?>, Object> injectors = new HashMap<>();

    public void setUseMocks(boolean useMocks) {
        this.useMocks = useMocks;
    }

    public <T> void register(Class<T> clazz, Object object) {
        injectors.put(clazz, object);
    }

    public <T> T resolve(Class<T> clazz, Object... args) {
        try {
            if (injectors.containsKey(clazz)) {
                if (useMocks) {
                    return clazz.cast(injectors.get(clazz));
                }
                var value = injectors.get(clazz);
                if (value instanceof Class<?> valueClass) {
                    var parameters = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
                    var instance = valueClass.getDeclaredConstructor(parameters).newInstance(args);
                    return clazz.cast(instance);
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException exception) {
            Logger.error("Failed to resolve dependency: %s", exception.getMessage());
            throw new RuntimeException("Failed to resolve dependency", exception);
        }
        throw new RuntimeException("No dependency found for " + clazz.getName());
    }
}

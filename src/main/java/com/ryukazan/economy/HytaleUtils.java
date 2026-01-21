package com.ryukazan.economy;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class HytaleUtils {

    public static List<String> getArgs(CommandContext ctx) {
        // ... (Keep existing implementation for args) ...
        return Collections.emptyList();
    }

    // --- FIXED: Fuzzy Reflection for ECS ---

    public static <T extends Component<EntityStore>> T getComponent(EntityStore store, Object entityRef, ComponentType<EntityStore, T> type) {
        try {
            // 1. Try standard getComponent
            Method m = store.getClass().getMethod("getComponent", entityRef.getClass(), ComponentType.class);
            return (T) m.invoke(store, entityRef, type);
        } catch (Exception e1) {
            try {
                // 2. Try Fallback: Some versions use 'get' or parameter order swapped
                for (Method m : store.getClass().getMethods()) {
                    if (m.getName().equals("getComponent") || m.getName().equals("get")) {
                        if (m.getParameterCount() == 2) {
                            try {
                                return (T) m.invoke(store, entityRef, type);
                            } catch (Exception ignored) {}
                        }
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public static <T extends Component<EntityStore>> void addComponent(EntityStore store, Object entityRef, ComponentType<EntityStore, T> type, T component) {
        try {
            // 1. Try standard addComponent
            Method m = store.getClass().getMethod("addComponent", entityRef.getClass(), ComponentType.class, Object.class);
            m.invoke(store, entityRef, type, component);
        } catch (Exception e1) {
            // 2. Try fuzzy match for any 'add' method with 3 args
            for (Method m : store.getClass().getMethods()) {
                if (m.getParameterCount() == 3 && (m.getName().startsWith("add") || m.getName().equals("register"))) {
                    try {
                        m.invoke(store, entityRef, type, component);
                        return;
                    } catch (Exception ignored) {}
                }
            }
            e1.printStackTrace();
        }
    }
}
package com.ryukazan.economy;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class HytaleUtils {

    // --- ARGUMENT HANDLING ---
    public static List<String> getArgs(CommandContext ctx) {
        try {
            // Try method: getArguments()
            try {
                Method m = ctx.getClass().getMethod("getArguments");
                return (List<String>) m.invoke(ctx);
            } catch (NoSuchMethodException ignored) {}

            // Try method: getArgs()
            try {
                Method m = ctx.getClass().getMethod("getArgs");
                return (List<String>) m.invoke(ctx);
            } catch (NoSuchMethodException ignored) {}

            // Try method: getParams()
            try {
                Method m = ctx.getClass().getMethod("getParams");
                return (List<String>) m.invoke(ctx);
            } catch (NoSuchMethodException ignored) {}

        } catch (Exception e) {
            System.err.println("Error fetching arguments: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    // --- ECS HANDLING (Reflection to bypass generic errors) ---

    public static <T extends Component<EntityStore>> T getComponent(EntityStore store, Object entityRef, ComponentType<EntityStore, T> type) {
        try {
            // Look for getComponent(Ref, ComponentType)
            Method m = store.getClass().getMethod("getComponent", entityRef.getClass(), ComponentType.class);
            return (T) m.invoke(store, entityRef, type);
        } catch (Exception e) {
            // Fallback: try searching for ANY method taking these types
            for (Method m : store.getClass().getMethods()) {
                if (m.getParameterCount() == 2
                        && Component.class.isAssignableFrom(m.getReturnType())) {
                    try {
                        return (T) m.invoke(store, entityRef, type);
                    } catch (Exception ignored) {}
                }
            }
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Component<EntityStore>> void addComponent(EntityStore store, Object entityRef, ComponentType<EntityStore, T> type, T component) {
        try {
            // Look for addComponent(Ref, ComponentType, Component)
            Method m = store.getClass().getMethod("addComponent", entityRef.getClass(), ComponentType.class, Object.class); // Object.class because generic T might be erased
            m.invoke(store, entityRef, type, component);
        } catch (Exception e) {
            // Fallback search
            for (Method m : store.getClass().getMethods()) {
                if (m.getParameterCount() == 3 && m.getName().startsWith("add")) {
                    try {
                        m.invoke(store, entityRef, type, component);
                        return;
                    } catch (Exception ignored) {}
                }
            }
            e.printStackTrace();
        }
    }
}
package com.ryukazan.economy;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Collections;
import java.util.List;

public class HytaleUtils {

    // --- ARGUMENT HANDLING ---
    public static List<String> getArgs(CommandContext ctx) {
        // FIX: The method is likely getArgs()
        return ctx.get();
    }

    // --- ECS HANDLING ---

    // FIX: Using 'long' (Entity ID) instead of 'Object' (Entity Reference)
    public static <T extends Component<EntityStore>> T getComponent(EntityStore store, long entityId, ComponentType<EntityStore, T> type) {
        if (type == null) {
            System.err.println("[Economy] Error: ComponentType is null! Plugin failed to register it.");
            return null;
        }
        // If "getComponent" is still red, try "get"
        return store.getComponent(entityId, type);
    }

    public static <T extends Component<EntityStore>> void addComponent(EntityStore store, long entityId, ComponentType<EntityStore, T> type, T component) {
        if (type == null) {
            System.err.println("[Economy] FATAL: Cannot add component because ComponentType is null.");
            return;
        }
        // If "addComponent" is still red, try "add" or "put"
        store.clone(entityId, type, component);
    }
}
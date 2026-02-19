package com.yigitguven.economy;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class HytaleUtils {

    // --- ECS HANDLING ---

    public static <T extends Component<EntityStore>> T getComponent(Player player, ComponentType<EntityStore, T> type) {
         if (type == null) {
             System.out.println("[EconomyDebug] getComponent: Type is NULL");
             return null;
         }
         var holder = player.toHolder();
         T comp = holder.getComponent(type);
         System.out.println("[EconomyDebug] getComponent for " + player.getDisplayName() + 
             " (Holder: " + holder.getClass().getSimpleName() + "@" + Integer.toHexString(holder.hashCode()) + "): " + comp);
         return comp;
    }

    public static <T extends Component<EntityStore>> void addComponent(Player player, ComponentType<EntityStore, T> type, T component) {
        if (type == null) {
            System.out.println("[EconomyDebug] addComponent: Type is NULL");
            return;
        }
        var holder = player.toHolder();
        System.out.println("[EconomyDebug] addComponent for " + player.getDisplayName() + 
            " (Holder: " + holder.getClass().getSimpleName() + "@" + Integer.toHexString(holder.hashCode()) + "): " + component);
        holder.addComponent(type, component);
    boolean has = holder.getComponent(type) != null;
    System.out.println("[EconomyDebug] addComponent post-check for " + player.getDisplayName() + ": hasComponent=" + has);
    }
}

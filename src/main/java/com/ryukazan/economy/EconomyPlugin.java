package com.ryukazan.economy;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;

public class EconomyPlugin extends JavaPlugin {

    public static EconomyPlugin INSTANCE;
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public EconomyPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        INSTANCE = this;
        LOGGER.atInfo().log("Economy Plugin Initializing...");
    }

    public void runSync(Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            LOGGER.atSevere().log("Error executing task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void setup() {
        // Initialize Component Type via Reflection
        try {
            if (MoneyComponent.TYPE == null) {
                // We use reflection to access the protected ComponentType constructor
                Constructor<ComponentType> constructor = ComponentType.class.getDeclaredConstructor(Class.class, String.class);
                constructor.setAccessible(true);
                MoneyComponent.TYPE = constructor.newInstance(MoneyComponent.class, "economy:money");
                LOGGER.atInfo().log("MoneyComponent Type registered successfully.");
            }
        } catch (Exception e) {
            // Corrected logging to ensure we see the error if it fails
            LOGGER.atSevere().log("Failed to initialize MoneyComponent Type! Error: " + e.toString());
            e.printStackTrace();
        }

        // Register Commands
        this.getCommandRegistry().registerCommand(new BalanceCommand());
        this.getCommandRegistry().registerCommand(new PayCommand());
        this.getCommandRegistry().registerCommand(new EcoAdminCommand());
    }
}
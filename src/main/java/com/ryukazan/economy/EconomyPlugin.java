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
        // --- FIXED: Robust Component Registration ---
        try {
            if (MoneyComponent.TYPE == null) {
                Constructor<?>[] constructors = ComponentType.class.getDeclaredConstructors();
                Constructor<?> bestMatch = null;

                // Loop through constructors to find one that matches our needs (Class, String) or similar
                for (Constructor<?> c : constructors) {
                    c.setAccessible(true);
                    if (c.getParameterCount() == 2) {
                        Class<?>[] types = c.getParameterTypes();
                        // Check for (Class, String) OR (String, Class)
                        if ((types[0] == Class.class && types[1] == String.class) ||
                                (types[0] == String.class && types[1] == Class.class)) {
                            bestMatch = c;
                            break;
                        }
                    }
                }

                if (bestMatch != null) {
                    // Handle parameter order dynamically
                    if (bestMatch.getParameterTypes()[0] == Class.class) {
                        MoneyComponent.TYPE = (ComponentType) bestMatch.newInstance(MoneyComponent.class, "economy:money");
                    } else {
                        MoneyComponent.TYPE = (ComponentType) bestMatch.newInstance("economy:money", MoneyComponent.class);
                    }
                    LOGGER.atInfo().log("MoneyComponent Type registered successfully.");
                } else {
                    LOGGER.atSevere().log("Could not find suitable constructor for ComponentType!");
                }
            }
        } catch (Exception e) {
            LOGGER.atSevere().log("Failed to initialize MoneyComponent Type! Error: " + e.toString());
            e.printStackTrace();
        }

        // Register Commands
        this.getCommandRegistry().registerCommand(new BalanceCommand());
        this.getCommandRegistry().registerCommand(new PayCommand());
        this.getCommandRegistry().registerCommand(new EcoAdminCommand());
    }
}
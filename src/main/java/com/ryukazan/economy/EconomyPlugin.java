package com.ryukazan.economy;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import javax.annotation.Nonnull;

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
        try {
            if (MoneyComponent.TYPE == null) {
                // FIX: Use the Constructor directly.
                // It likely takes (String name, Class<T> clazz, Supplier<T> factory)
                // If this line is still red, put your cursor on "ComponentType" and press Ctrl+P (or Cmd+P) to see the expected arguments.
                MoneyComponent.TYPE = new ComponentType<>(
                        "economy:money",
                        MoneyComponent.class,
                        MoneyComponent::new
                );

                LOGGER.atInfo().log("MoneyComponent Type registered successfully.");
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
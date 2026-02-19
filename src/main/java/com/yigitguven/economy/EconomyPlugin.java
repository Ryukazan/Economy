package com.yigitguven.economy;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
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
                MoneyComponent.TYPE = EntityStore.REGISTRY.registerComponent(
                    MoneyComponent.class,
                    "economy:money",
                    com.hypixel.hytale.codec.builder.BuilderCodec.builder(MoneyComponent.class, MoneyComponent::new)
                        .addField(new com.hypixel.hytale.codec.KeyedCodec<>("Balance", com.hypixel.hytale.codec.Codec.LONG), (c, v) -> c.balance = v, (c) -> c.balance)
                        .build(),
                    true
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

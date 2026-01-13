package com.yigit.economy;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import javax.annotation.Nonnull;

public class Main extends JavaPlugin {

    private final JavaPluginInit init;

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
        this.init = init;
    }

    @Override
    protected void setup() {
        super.setup();

        // Register Commands
        this.getCommandRegistry().registerCommand(new BalanceCommand());
        this.getCommandRegistry().registerCommand(new GiveMoneyCommand());

        System.out.println("Economy Mod: Commands Registered.");

        // Attempt to register components via the server registry if available.
        // If the registry cannot be accessed here, the MoneyComponent type
        // will be initialized lazily by the commands.
        try {
            // Future implementation: Check init.getServer() for direct registry access
        } catch (Exception e) {
            System.out.println("Economy Mod: Manual registry access skipped. Using lazy initialization.");
        }
    }
}
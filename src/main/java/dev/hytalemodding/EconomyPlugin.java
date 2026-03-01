package dev.hytalemodding;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.hytalemodding.commands.AdminEconomyCommand;
import dev.hytalemodding.commands.BalanceCommand;
import dev.hytalemodding.commands.PayCommand;

import javax.annotation.Nonnull;
import java.io.File;

public class EconomyPlugin extends JavaPlugin {
    private EconomyManager economyManager;

    public EconomyPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        // Initialize Economy Manager
        File dataFolder = this.getDataDirectory().toFile();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.economyManager = new EconomyManager(dataFolder);

        // Register Commands
        this.getCommandRegistry().registerCommand(new BalanceCommand(this));
        this.getCommandRegistry().registerCommand(new PayCommand(this));
        this.getCommandRegistry().registerCommand(new AdminEconomyCommand(this));

        // Register Events
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            // Ensure player has a balance entry when they join
            this.economyManager.getBalance(event.getPlayer().getUuid());
        });
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }
}

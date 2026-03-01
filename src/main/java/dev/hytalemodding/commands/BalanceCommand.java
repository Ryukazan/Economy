package dev.hytalemodding.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.hytalemodding.EconomyPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class BalanceCommand extends AbstractCommand {
    private final EconomyPlugin plugin;

    public BalanceCommand(EconomyPlugin plugin) {
        super("money", "Check your balance");
        this.plugin = plugin;
    }

    @Nullable
    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!context.isPlayer()) {
            context.sendMessage(Message.raw("Only players can use this command."));
            return CompletableFuture.completedFuture(null);
        }

        double balance = plugin.getEconomyManager().getBalance(context.sender().getUuid());
        context.sendMessage(Message.raw("Your balance: $" + String.format("%.2f", balance)));
        
        return CompletableFuture.completedFuture(null);
    }
}

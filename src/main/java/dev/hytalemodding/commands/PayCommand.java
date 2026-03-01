package dev.hytalemodding.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import dev.hytalemodding.EconomyPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class PayCommand extends AbstractCommand {
    private final EconomyPlugin plugin;
    private final RequiredArg<String> targetPlayerArg;
    private final RequiredArg<Double> amountArg;

    public PayCommand(EconomyPlugin plugin) {
        super("pay", "Pay money to another player");
        this.plugin = plugin;
        this.targetPlayerArg = withRequiredArg("player", "Target player", ArgTypes.STRING);
        this.amountArg = withRequiredArg("amount", "Amount to pay", ArgTypes.DOUBLE);
    }

    @Nullable
    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!context.isPlayer()) {
            context.sendMessage(Message.raw("Only players can use this command."));
            return CompletableFuture.completedFuture(null);
        }

        String targetName = targetPlayerArg.get(context);
        double amount = amountArg.get(context);

        if (amount <= 0) {
            context.sendMessage(Message.raw("Invalid amount."));
            return CompletableFuture.completedFuture(null);
        }

        PlayerRef sender = context.senderAsPlayerRef().getStore().getComponent(context.senderAsPlayerRef(), Universe.get().getPlayerRefComponentType());
        PlayerRef target = Universe.get().getPlayerByUsername(targetName, NameMatching.EXACT);

        if (target == null) {
            context.sendMessage(Message.raw("Player not found."));
            return CompletableFuture.completedFuture(null);
        }

        if (target.getUuid().equals(sender.getUuid())) {
            context.sendMessage(Message.raw("You cannot pay yourself."));
            return CompletableFuture.completedFuture(null);
        }

        if (plugin.getEconomyManager().removeMoney(sender.getUuid(), amount)) {
            plugin.getEconomyManager().addMoney(target.getUuid(), amount);
            context.sendMessage(Message.raw("You paid $" + amount + " to " + target.getUsername()));
            target.sendMessage(Message.raw("You received $" + amount + " from " + sender.getUsername()));
        } else {
            context.sendMessage(Message.raw("Insufficient funds."));
        }

        return CompletableFuture.completedFuture(null);
    }
}

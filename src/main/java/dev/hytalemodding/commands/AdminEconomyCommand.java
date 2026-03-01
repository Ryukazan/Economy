package dev.hytalemodding.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import dev.hytalemodding.EconomyPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class AdminEconomyCommand extends AbstractCommand {
    private final EconomyPlugin plugin;
    private final RequiredArg<String> actionArg;
    private final RequiredArg<String> targetPlayerArg;
    private final OptionalArg<Double> amountArg;

    public AdminEconomyCommand(EconomyPlugin plugin) {
        super("eco", "Admin economy commands");
        this.plugin = plugin;
        this.actionArg = withRequiredArg("action", "give|set|reset", ArgTypes.STRING);
        this.targetPlayerArg = withRequiredArg("player", "Target player", ArgTypes.STRING);
        this.amountArg = withOptionalArg("amount", "Amount", ArgTypes.DOUBLE);
    }

    @Nullable
    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        if (!context.sender().hasPermission("economy.admin")) {
            context.sendMessage(Message.raw("You do not have permission to use this command."));
            return CompletableFuture.completedFuture(null);
        }

        String action = actionArg.get(context).toLowerCase();
        String targetName = targetPlayerArg.get(context);
        PlayerRef target = Universe.get().getPlayerByUsername(targetName, NameMatching.EXACT);

        if (target == null) {
            context.sendMessage(Message.raw("Player not found."));
            return CompletableFuture.completedFuture(null);
        }

        switch (action) {
            case "give":
                if (!amountArg.provided(context)) break;
                double amountGive = amountArg.get(context);
                plugin.getEconomyManager().addMoney(target.getUuid(), amountGive);
                context.sendMessage(Message.raw("Gave $" + amountGive + " to " + target.getUsername()));
                return CompletableFuture.completedFuture(null);
            case "set":
                if (!amountArg.provided(context)) break;
                double amountSet = amountArg.get(context);
                plugin.getEconomyManager().setBalance(target.getUuid(), amountSet);
                context.sendMessage(Message.raw("Set balance of " + target.getUsername() + " to $" + amountSet));
                return CompletableFuture.completedFuture(null);
            case "reset":
                plugin.getEconomyManager().setBalance(target.getUuid(), 100.0);
                context.sendMessage(Message.raw("Reset balance of " + target.getUsername()));
                return CompletableFuture.completedFuture(null);
        }

        context.sendMessage(Message.raw("Usage: /eco <give|set|reset> <player> [amount]"));
        return CompletableFuture.completedFuture(null);
    }
}

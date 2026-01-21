package com.ryukazan.economy;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.awt.Color;
import javax.annotation.Nonnull;

public class EcoAdminCommand extends CommandBase {

    private final RequiredArg<String> actionArg;
    private final RequiredArg<String> playerArg;
    private final RequiredArg<Integer> amountArg;

    public EcoAdminCommand() {
        super("ecoadmin", "Manage economy.");
        this.setPermissionGroup(GameMode.Creative);
        this.actionArg = withRequiredArg("action", "give|take|set", ArgTypes.STRING);
        this.playerArg = withRequiredArg("player", "Target player", ArgTypes.STRING);
        this.amountArg = withRequiredArg("amount", "Quantity", ArgTypes.INTEGER);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        String action = actionArg.get(ctx);
        String targetName = playerArg.get(ctx);
        int amount = amountArg.get(ctx);

        Player target = null;

        // Try to find player in the sender's world
        if (ctx.sender() instanceof Player senderPlayer) {
            for (Player p : senderPlayer.getWorld().getPlayers()) {
                // FIXED: getName() -> getDisplayName()
                if (p.getDisplayName().equalsIgnoreCase(targetName)) {
                    target = p;
                    break;
                }
            }
        }

        if (target == null) {
            ctx.sendMessage(Message.raw("Player '" + targetName + "' not found.").color(Color.RED));
            return;
        }

        final Player finalTarget = target;

        EconomyPlugin.INSTANCE.runSync(() -> {
            EntityStore store = finalTarget.getWorld().getEntityStore();
            MoneyComponent wallet = HytaleUtils.getComponent(store, finalTarget.getReference(), MoneyComponent.TYPE);

            if (wallet == null) {
                wallet = new MoneyComponent(0);
                HytaleUtils.addComponent(store, finalTarget.getReference(), MoneyComponent.TYPE, wallet);
            }

            if (action.equalsIgnoreCase("give")) {
                wallet.add(amount);
                ctx.sendMessage(Message.raw("Gave " + amount + " coins to " + finalTarget.getDisplayName()).color(Color.GREEN));
            } else if (action.equalsIgnoreCase("take")) {
                wallet.remove(amount);
                ctx.sendMessage(Message.raw("Took " + amount + " coins from " + finalTarget.getDisplayName()).color(Color.GREEN));
            } else if (action.equalsIgnoreCase("set")) {
                wallet.setBalance(amount);
                ctx.sendMessage(Message.raw("Set " + finalTarget.getDisplayName() + " balance to " + amount).color(Color.GREEN));
            }
        });
    }
}
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

public class PayCommand extends CommandBase {

    private final RequiredArg<String> playerArg;
    private final RequiredArg<Integer> amountArg;

    public PayCommand() {
        super("pay", "Send money to another player.");
        this.setPermissionGroup(GameMode.Adventure);
        this.playerArg = withRequiredArg("player", "Target player", ArgTypes.STRING);
        this.amountArg = withRequiredArg("amount", "Quantity", ArgTypes.INTEGER);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        if (!(ctx.sender() instanceof Player sender)) {
            ctx.sendMessage(Message.raw("Only players can use this command."));
            return;
        }

        String targetName = playerArg.get(ctx);
        int amount = amountArg.get(ctx);

        if (amount <= 0) {
            sender.sendMessage(Message.raw("Amount must be positive.").color(Color.RED));
            return;
        }

        Player target = null;
        for (Player p : sender.getWorld().getPlayers()) {
            // FIXED: getName() -> getDisplayName()
            if (p.getDisplayName().equalsIgnoreCase(targetName)) {
                target = p;
                break;
            }
        }

        if (target == null) {
            sender.sendMessage(Message.raw("Player not found.").color(Color.RED));
            return;
        }

        if (target.equals(sender)) {
            sender.sendMessage(Message.raw("You cannot pay yourself.").color(Color.RED));
            return;
        }

        final Player finalTarget = target;

        EconomyPlugin.INSTANCE.runSync(() -> {
            EntityStore store = sender.getWorld().getEntityStore();
            MoneyComponent senderWallet = HytaleUtils.getComponent(store, sender.getReference(), MoneyComponent.TYPE);
            MoneyComponent targetWallet = HytaleUtils.getComponent(store, finalTarget.getReference(), MoneyComponent.TYPE);

            if (senderWallet == null) {
                senderWallet = new MoneyComponent(0);
                HytaleUtils.addComponent(store, sender.getReference(), MoneyComponent.TYPE, senderWallet);
            }
            if (targetWallet == null) {
                targetWallet = new MoneyComponent(0);
                HytaleUtils.addComponent(store, finalTarget.getReference(), MoneyComponent.TYPE, targetWallet);
            }

            if (senderWallet.remove(amount)) {
                targetWallet.add(amount);
                sender.sendMessage(Message.raw("Sent " + amount + " coins to " + finalTarget.getDisplayName()).color(Color.GREEN));
                finalTarget.sendMessage(Message.raw("Received " + amount + " coins from " + sender.getDisplayName()).color(Color.GREEN));
            } else {
                sender.sendMessage(Message.raw("Insufficient funds.").color(Color.RED));
            }
        });
    }
}
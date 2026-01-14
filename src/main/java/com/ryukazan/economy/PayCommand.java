package com.ryukazan.economy;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.awt.Color;
import java.util.List;
import javax.annotation.Nonnull;

public class PayCommand extends CommandBase {

    public PayCommand() {
        super("pay", "Send money to another player.");
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        if (!(ctx.sender() instanceof Player sender)) {
            ctx.sendMessage(Message.raw("Only players can use this command."));
            return;
        }

        // USE UTILS
        List<String> args = HytaleUtils.getArgs(ctx);

        if (args.size() < 2) {
            sender.sendMessage(Message.raw("Usage: /pay <player> <amount>").color(Color.RED));
            return;
        }

        String targetName = args.get(0);
        long amount;

        try {
            amount = Long.parseLong(args.get(1));
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(Message.raw("Invalid amount.").color(Color.RED));
            return;
        }

        Player target = null;
        for (Player p : sender.getWorld().getPlayers()) {
            if (p.toString().toLowerCase().contains(targetName.toLowerCase())) {
                target = p;
                break;
            }
        }

        if (target == null) {
            sender.sendMessage(Message.raw("Player not found.").color(Color.RED));
            return;
        }

        final Player finalTarget = target;

        EconomyPlugin.INSTANCE.runSync(() -> {
            EntityStore store = sender.getWorld().getEntityStore();

            // USE UTILS
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
                sender.sendMessage(Message.raw("Sent " + amount + " coins.").color(Color.GREEN));
                finalTarget.sendMessage(Message.raw("Received " + amount + " coins.").color(Color.GREEN));
            } else {
                sender.sendMessage(Message.raw("Insufficient funds.").color(Color.RED));
            }
        });
    }
}
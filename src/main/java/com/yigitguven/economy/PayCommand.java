package com.yigitguven.economy;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
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
    @SuppressWarnings("removal")
    protected void executeSync(@Nonnull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("Only players can use this command."));
            return;
        }
        
        Player sender = (Player) ctx.sender();

        String targetName = ctx.get(playerArg);
        Integer amountRaw = ctx.get(amountArg);
        if (amountRaw == null) amountRaw = 0;
        final int amount = amountRaw;

        if (amount <= 0) {
            sender.sendMessage(Message.raw("Amount must be positive.").color(Color.RED));
            return;
        }

        Player target = null;

        // Lookup logic: Try to find a player by name in the sender's world
        for (Player p : sender.getWorld().getPlayers()) {
             if (p.getPlayerRef().getUsername().equalsIgnoreCase(targetName) || p.getDisplayName().equalsIgnoreCase(targetName)) {
                 target = p;
                 break;
             }
        }

        if (target == null) {
             if (sender.getDisplayName().equalsIgnoreCase(targetName)) {
                 sender.sendMessage(Message.raw("You cannot pay yourself.").color(Color.RED));
                 return;
             }
             sender.sendMessage(Message.raw("Player '" + targetName + "' not found (only checking current world).").color(Color.RED));
             return;
        }

        final Player finalTarget = target;

        EconomyPlugin.INSTANCE.runSync(() -> {
            System.out.println("[EconomyDebug] /pay: " + sender.getDisplayName() + " -> " + finalTarget.getDisplayName() + " amount: " + amount);
            MoneyComponent senderWallet = HytaleUtils.getComponent(sender, MoneyComponent.TYPE);
            MoneyComponent targetWallet = HytaleUtils.getComponent(finalTarget, MoneyComponent.TYPE);

            if (senderWallet == null) {
                System.out.println("[EconomyDebug] /pay: Sender has no wallet.");
                senderWallet = new MoneyComponent(0);
                HytaleUtils.addComponent(sender, MoneyComponent.TYPE, senderWallet);
            }
            if (targetWallet == null) {
                System.out.println("[EconomyDebug] /pay: Target has no wallet.");
                targetWallet = new MoneyComponent(0);
                HytaleUtils.addComponent(finalTarget, MoneyComponent.TYPE, targetWallet);
            }

            if (senderWallet.remove(amount)) {
                targetWallet.add(amount);
                
                System.out.println("[EconomyDebug] /pay: Transfer success. Sender New Bal: " + senderWallet.balance + ", Target New Bal: " + targetWallet.balance);

                // FORCE UPDATE: Persist changes
                HytaleUtils.addComponent(sender, MoneyComponent.TYPE, senderWallet);
                HytaleUtils.addComponent(finalTarget, MoneyComponent.TYPE, targetWallet);
                
                sender.sendMessage(Message.raw("Sent " + amount + " coins to " + finalTarget.getPlayerRef().getUsername()).color(Color.GREEN));
                // Assuming target MessageReceiver works via PlayerRef
                finalTarget.sendMessage(Message.raw("Received " + amount + " coins from " + sender.getDisplayName()).color(Color.GREEN));
            } else {
                System.out.println("[EconomyDebug] /pay: Insufficient funds. Sender Bal: " + senderWallet.balance);
                sender.sendMessage(Message.raw("Insufficient funds.").color(Color.RED));
            }
        });
    }
}

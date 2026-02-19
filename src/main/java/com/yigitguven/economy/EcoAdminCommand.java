package com.yigitguven.economy;

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
        String action = ctx.get(actionArg);
        String targetName = ctx.get(playerArg);
        Integer amountRaw = ctx.get(amountArg);
        
        int amount = (amountRaw == null || amountRaw <= 0) ? 1 : amountRaw;

        Player target = null;
        
        // Lookup logic: Try to use HytaleServer to find a player by name
        // We iterate over all players in the sender's world as a fallback.
        
        if (ctx.isPlayer()) {
             com.hypixel.hytale.server.core.entity.entities.Player senderPlayer = (com.hypixel.hytale.server.core.entity.entities.Player) ctx.sender();
             for (com.hypixel.hytale.server.core.entity.entities.Player p : senderPlayer.getWorld().getPlayers()) {
                 if (p.getPlayerRef().getUsername().equalsIgnoreCase(targetName) || p.getDisplayName().equalsIgnoreCase(targetName)) {
                     target = p;
                     break;
                 }
             }
        }
        
        if (target == null) {
             ctx.sendMessage(Message.raw("Player '" + targetName + "' not found (only checking current world).").color(Color.RED));
             return;
        }

        final com.hypixel.hytale.server.core.entity.entities.Player finalTarget = target;
        final int finalAmount = amount;

        EconomyPlugin.INSTANCE.runSync(() -> {
            System.out.println("[EconomyDebug] /ecoadmin " + action + " on " + finalTarget.getDisplayName() + " amount: " + finalAmount);
            MoneyComponent wallet = HytaleUtils.getComponent(finalTarget, MoneyComponent.TYPE);

            if (wallet == null) {
                System.out.println("[EconomyDebug] /ecoadmin: Target has no wallet. Creating new.");
                wallet = new MoneyComponent(0);
                HytaleUtils.addComponent(finalTarget, MoneyComponent.TYPE, wallet);
            }

            boolean updated = false;
            
            if (action.equalsIgnoreCase("give")) {
                wallet.add(finalAmount);
                updated = true;
                ctx.sendMessage(Message.raw("Gave " + finalAmount + " coins to " + finalTarget.getPlayerRef().getUsername()).color(Color.GREEN));
            } else if (action.equalsIgnoreCase("take")) {
                if (wallet.remove(finalAmount)) {
                    updated = true;
                    ctx.sendMessage(Message.raw("Took " + finalAmount + " coins from " + finalTarget.getPlayerRef().getUsername()).color(Color.GREEN));
                } else {
                    ctx.sendMessage(Message.raw("Player has insufficient funds.").color(Color.RED));
                }
            } else if (action.equalsIgnoreCase("set")) {
                wallet.setBalance(finalAmount);
                updated = true;
                ctx.sendMessage(Message.raw("Set " + finalTarget.getPlayerRef().getUsername() + " balance to " + finalAmount).color(Color.GREEN));
            }
            
            System.out.println("[EconomyDebug] /ecoadmin: Updated? " + updated + ". New Balance: " + wallet.balance);

            if (updated) {
                // FORCE UPDATE: Re-add the component to ensure ECS detects the change and persists it.
                HytaleUtils.addComponent(finalTarget, MoneyComponent.TYPE, wallet);
            }
        });
    }
}

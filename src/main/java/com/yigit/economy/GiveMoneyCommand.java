package com.yigit.economy;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.Message;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import java.awt.Color;
import java.util.concurrent.CompletableFuture;

public class GiveMoneyCommand extends AbstractCommand {

    private final RequiredArg<String> targetArg;
    private final RequiredArg<Integer> amountArg;

    public GiveMoneyCommand() {
        super("givemoney", "Transfer money to another player", false);
        this.addAliases("pay", "transfer");

        var playerType = new SingleArgumentType<String>("target", "The player name") {
            @NullableDecl @Override public String parse(String s, ParseResult parseResult) { return s; }
        };

        var intType = new SingleArgumentType<Integer>("amount", "Amount to send") {
            @NullableDecl @Override public Integer parse(String s, ParseResult parseResult) {
                try { return Integer.parseInt(s); } catch (Exception e) { return null; }
            }
        };

        this.targetArg = this.withRequiredArg("target", "The player to give money to", playerType);
        this.amountArg = this.withRequiredArg("amount", "Amount to send", intType);
    }

    @Nullable
    @Override
    @SuppressWarnings("removal")
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        CommandSender sender = context.sender();

        if (sender instanceof Player) {
            Player senderPlayer = (Player) sender;

            // Initialization Check
            if (MoneyComponent.TYPE == null) {
                senderPlayer.sendMessage(Message.raw("Error: Economy Mod not initialized properly.").color(Color.RED));
                return CompletableFuture.completedFuture(null);
            }

            String targetName = context.get(this.targetArg);
            Integer amount = context.get(this.amountArg);

            if (amount == null || amount <= 0) {
                senderPlayer.sendMessage(Message.raw("Invalid amount.").color(Color.RED));
                return CompletableFuture.completedFuture(null);
            }

            // Fallback player lookup using string representation
            Player targetPlayer = null;
            for (Player p : senderPlayer.getWorld().getPlayers()) {
                if (p.toString().toLowerCase().contains(targetName.toLowerCase())) {
                    targetPlayer = p;
                    break;
                }
            }

            if (targetPlayer == null) {
                senderPlayer.sendMessage(Message.raw("Player not found: " + targetName).color(Color.RED));
                return CompletableFuture.completedFuture(null);
            }

            Ref<EntityStore> senderRef = senderPlayer.getReference();
            Ref<EntityStore> targetRef = targetPlayer.getReference();

            if (senderRef != null && senderRef.isValid() && targetRef != null && targetRef.isValid()) {
                Store<EntityStore> store = senderRef.getStore();

                MoneyComponent senderWallet = store.getComponent(senderRef, MoneyComponent.TYPE);
                MoneyComponent targetWallet = store.getComponent(targetRef, MoneyComponent.TYPE);

                // Lazy Initialization
                if (senderWallet == null) {
                    senderWallet = new MoneyComponent(0L);
                    store.addComponent(senderRef, MoneyComponent.TYPE, senderWallet);
                }
                if (targetWallet == null) {
                    targetWallet = new MoneyComponent(0L);
                    store.addComponent(targetRef, MoneyComponent.TYPE, targetWallet);
                }

                if (senderWallet.remove(amount)) {
                    targetWallet.add(amount);
                    senderPlayer.sendMessage(Message.raw("Sent " + amount + " coins.").color(Color.GREEN));
                    targetPlayer.sendMessage(Message.raw("Received " + amount + " coins.").color(Color.GREEN));
                } else {
                    senderPlayer.sendMessage(Message.raw("Insufficient funds.").color(Color.RED));
                }
            }
        }
        return CompletableFuture.completedFuture(null);
    }
}
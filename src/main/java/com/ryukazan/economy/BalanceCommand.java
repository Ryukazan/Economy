package com.ryukazan.economy;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.awt.Color;
import javax.annotation.Nonnull;

public class BalanceCommand extends CommandBase {

    public BalanceCommand() {
        super("balance", "Check your wallet balance.");
        this.addAliases("bal", "money", "wallet");
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        if (!(ctx.sender() instanceof Player player)) {
            ctx.sendMessage(Message.raw("Only players can use this command."));
            return;
        }

        EconomyPlugin.INSTANCE.runSync(() -> {
            EntityStore store = player.getWorld().getEntityStore();

            // USE UTILS
            MoneyComponent money = HytaleUtils.getComponent(store, player.getReference(), MoneyComponent.TYPE);

            if (money == null) {
                money = new MoneyComponent(0);
                HytaleUtils.addComponent(store, player.getReference(), MoneyComponent.TYPE, money);
            }

            long balance = money.balance;
            player.sendMessage(Message.raw("Your Balance: " + balance + " Coins").color(Color.GREEN));
        });
    }
}
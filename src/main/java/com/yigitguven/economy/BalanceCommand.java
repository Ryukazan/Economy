package com.yigitguven.economy;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.awt.Color;
import javax.annotation.Nonnull;

public class BalanceCommand extends CommandBase {

    public BalanceCommand() {
        super("balance", "Check your wallet balance.");
        this.addAliases("bal", "money", "wallet");
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    @SuppressWarnings("removal")
    protected void executeSync(@Nonnull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("Only players can use this command."));
            return;
        }
        
        // Resolve Player and Ref
        Player player = (Player) ctx.sender();
        PlayerRef ref = player.getPlayerRef();

        EconomyPlugin.INSTANCE.runSync(() -> {
            System.out.println("[EconomyDebug] /bal execution for " + player.getDisplayName());
            MoneyComponent money = HytaleUtils.getComponent(player, MoneyComponent.TYPE);

            if (money == null) {
                 System.out.println("[EconomyDebug] /bal: No MoneyComponent found. Creating new one.");
                money = new MoneyComponent(0);
                HytaleUtils.addComponent(player, MoneyComponent.TYPE, money);
            }

            long balance = money.balance;
            System.out.println("[EconomyDebug] /bal: Current Balance = " + balance);
            player.sendMessage(Message.raw("Your Balance: " + balance + " Coins").color(Color.GREEN));
        });
    }
}

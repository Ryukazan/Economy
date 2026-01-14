package com.yigit.economy;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.ComponentType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.concurrent.CompletableFuture;

public class BalanceCommand extends AbstractCommand {

    public BalanceCommand() {
        super("balance", "Check your current money balance", false);
        this.addAliases("bal", "money", "wallet");
    }

    @Nullable
    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        CommandSender sender = context.sender();

        if (sender instanceof Player player) {
            Ref<EntityStore> ref = player.getReference();
            if (ref != null && ref.isValid()) {
                Store<EntityStore> store = ref.getStore();

                // FIX: If TYPE is null, force-create it using Reflection
                if (MoneyComponent.TYPE == null) {
                    try {
                        java.lang.reflect.Constructor<ComponentType> constructor = ComponentType.class.getDeclaredConstructor(Class.class, String.class);
                        constructor.setAccessible(true);
                        MoneyComponent.TYPE = constructor.newInstance(MoneyComponent.class, "economy:money");
                    } catch (Exception e) {
                        player.sendMessage(Message.raw("Critical Error: Could not initialize Economy.").color(Color.RED));
                        e.printStackTrace();
                        return CompletableFuture.completedFuture(null);
                    }
                }

                MoneyComponent money = store.getComponent(ref, MoneyComponent.TYPE);

                if (money == null) {
                    money = new MoneyComponent(0L);
                    store.addComponent(ref, MoneyComponent.TYPE, money);
                }

                player.sendMessage(Message.raw("Your Balance: " + money.getBalance() + " coins").color(Color.GREEN));
            }
        } else {
            sender.sendMessage(Message.raw("Only players can check their balance."));
        }

        return CompletableFuture.completedFuture(null);
    }
}
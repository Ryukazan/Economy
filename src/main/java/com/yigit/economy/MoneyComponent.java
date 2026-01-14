package com.yigit.economy;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class MoneyComponent implements Component<EntityStore> {

    public static ComponentType<EntityStore, MoneyComponent> TYPE;

    public static final Codec<MoneyComponent> CODEC = BuilderCodec.builder(MoneyComponent.class, MoneyComponent::new)
            .addField(
                    new KeyedCodec<>("Balance", Codec.LONG),
                    (MoneyComponent c, Long v) -> c.setBalance(v),
                    (MoneyComponent c) -> c.getBalance()
            )
            .build();

    private Long balance;

    public MoneyComponent() { this.balance = 0L; }
    public MoneyComponent(Long balance) { this.balance = balance; }

    public Long getBalance() { return balance; }
    public void setBalance(Long balance) { this.balance = balance; }

    public void add(long amount) { this.balance += amount; }

    public boolean remove(long amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public MoneyComponent clone() {
        return new MoneyComponent(this.balance);
    }
}
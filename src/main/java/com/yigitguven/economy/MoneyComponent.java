package com.yigitguven.economy;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class MoneyComponent implements Component<EntityStore> {
    public long balance = 0;
    public static ComponentType<EntityStore, MoneyComponent> TYPE;

    public MoneyComponent() { this.balance = 0; }
    public MoneyComponent(long balance) { this.balance = balance; }

    public void add(long amount) { this.balance += amount; }
    public boolean remove(long amount) {
        if (this.balance >= amount) { this.balance -= amount; return true; }
        return false;
    }
    public void setBalance(long amount) { this.balance = amount; }

    @Override
    public MoneyComponent clone() { return new MoneyComponent(this.balance); }

    public MoneyComponent copy() { return new MoneyComponent(this.balance); }

    @Override
    public String toString() { return "MoneyComponent{balance=" + balance + "}"; }
}

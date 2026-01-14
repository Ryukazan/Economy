package com.yigit.economy;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.component.ComponentType;
import javax.annotation.Nonnull;

public class Main extends JavaPlugin {

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        super.setup();

        this.getCommandRegistry().registerCommand(new BalanceCommand());
        this.getCommandRegistry().registerCommand(new GiveMoneyCommand());

        try {
            if (MoneyComponent.TYPE == null) {
                java.lang.reflect.Constructor<ComponentType> constructor = ComponentType.class.getDeclaredConstructor(Class.class, String.class);
                constructor.setAccessible(true);
                MoneyComponent.TYPE = constructor.newInstance(MoneyComponent.class, "economy:money");
                System.out.println("Economy: MoneyComponent initialized via Reflection!");
            }
        } catch (Exception e) {
            System.out.println("Economy Warning: Could not initialize Type in Main (will try in commands). " + e.getMessage());
        }
    }
}
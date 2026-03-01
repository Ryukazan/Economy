package dev.hytalemodding;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EconomyManager {
    private static final Logger LOGGER = Logger.getLogger(EconomyManager.class.getName());
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File storageFile;
    private final Map<UUID, Double> balances = new HashMap<>();

    public EconomyManager(File dataFolder) {
        this.storageFile = new File(dataFolder, "balances.json");
        load();
    }

    public double getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, 100.0); // Default starting balance
    }

    public void setBalance(UUID uuid, double amount) {
        balances.put(uuid, amount);
        save();
    }

    public void addMoney(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public boolean removeMoney(UUID uuid, double amount) {
        double current = getBalance(uuid);
        if (current < amount) return false;
        setBalance(uuid, current - amount);
        return true;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(storageFile)) {
            GSON.toJson(balances, writer);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not save balances", e);
        }
    }

    public void load() {
        if (!storageFile.exists()) return;
        try (FileReader reader = new FileReader(storageFile)) {
            Type type = new TypeToken<HashMap<UUID, Double>>() {}.getType();
            Map<UUID, Double> loaded = GSON.fromJson(reader, type);
            if (loaded != null) {
                balances.putAll(loaded);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not load balances", e);
        }
    }
}

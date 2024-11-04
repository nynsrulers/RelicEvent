package com.aelithron.relicevent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class RelicDumper {
    private RelicEvent plugin;
    public RelicDumper(RelicEvent plugin) {
        this.plugin = plugin;
    }

    public void dumpRelics() {
        List<String> foundRelics = new ArrayList<>();
        List<String> notFoundRelics = new ArrayList<>();

        plugin.reloadDataStore();
        for (String relicKey : plugin.dataStore.getConfigurationSection("Relics").getKeys(false)) {
            if (plugin.dataStore.getBoolean("Relics." + relicKey + ".found")) {
                foundRelics.add(relicKey);
            } else {
                notFoundRelics.add(relicKey);
            }
        }

        String filename = "relic-dump-" + Instant.now().getEpochSecond() + ".txt";

        // Create the file
        File file = new File(plugin.getDataFolder(), filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write the "Found" section
            writer.write("=== Found ===\n");
            for (String relic : foundRelics) {
                int x = plugin.dataStore.getInt("Relics." + relic + ".x");
                int y = plugin.dataStore.getInt("Relics." + relic + ".y");
                int z = plugin.dataStore.getInt("Relics." + relic + ".z");
                String world = plugin.dataStore.getString("Relics." + relic + ".world");
                writer.write(relic + ": " + x + ", " + y + ", " + z + ", " + world + "\n");
            }

            // Write the "Not Found" section
            writer.write("\n=== Not Found ===\n");
            for (String relic : notFoundRelics) {
                int x = plugin.dataStore.getInt("Relics." + relic + ".x");
                int y = plugin.dataStore.getInt("Relics." + relic + ".y");
                int z = plugin.dataStore.getInt("Relics." + relic + ".z");
                String world = plugin.dataStore.getString("Relics." + relic + ".world");
                writer.write(relic + ": " + x + ", " + y + ", " + z + ", " + world + "\n");
            }

            writer.flush();
            plugin.getLogger().info("Relic dump created: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Failed to write relic dump.");
        }
    }
}


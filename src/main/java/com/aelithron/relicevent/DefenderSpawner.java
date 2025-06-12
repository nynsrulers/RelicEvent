package com.aelithron.relicevent;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Zombie;
import org.bukkit.loot.LootTables;

public class DefenderSpawner {
    private RelicEvent plugin;
    public DefenderSpawner(RelicEvent plugin) {
        this.plugin = plugin;
    }

    public void SpawnDefenders(String relicID) {
        Location spawnLoc = new Location(
                plugin.getServer().getWorld(plugin.dataStore.getString("Relics." + relicID + ".world")),
                plugin.dataStore.getInt("Relics." + relicID + ".x"),
                plugin.dataStore.getInt("Relics." + relicID + ".y" + 1),
                plugin.dataStore.getInt("Relics." + relicID + ".z"));

        for (int i = 0; i < 3; i++) {
            Zombie defender = spawnLoc.getWorld().spawn(spawnLoc, Zombie.class);
            defender.setCustomName(ChatColor.GOLD + "Relic Defender");
            defender.setCustomNameVisible(true);
            NBT.modifyPersistentData(defender, nbt -> {
                nbt.setBoolean("RE_IsRelicDefender", true);
                nbt.setString("RE_RelicID", relicID);
            });
        }
    }
}


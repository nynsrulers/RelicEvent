package com.aelithron.relicevent;

import de.tr7zw.nbtapi.NBT;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class RelicEvent extends JavaPlugin {
    // Files
    YamlConfiguration dataStore;
    // Actual "File" objects (used for reloading)
    private File dataStoreFile;
    // Vault
    private static Economy econ = null;
    // Bloodforged array
    public Map<UUID, UUID> bloodforged = new HashMap<>();

    @Override
    public void onEnable() {
        // Events
        getServer().getPluginManager().registerEvents(new RelicListener(this), this);
        // General Config
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        // Data Storage
        dataStoreFile = new File(getDataFolder(), "data.yml");
        if (!dataStoreFile.exists()) {
            saveResource("data.yml", false);
        }
        reloadDataStore();
        // Commands
        getCommand("relics").setExecutor(new RelicCMD(this));
        getCommand("relicmgr").setExecutor(new RelicAdminCMD(this));
        // Vault
        if (getConfig().getBoolean("UseEconomy") && !setupEconomy()) {
            getLogger().severe("Couldn't set up the economy API (with Vault)!");
            return;
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public String getPrefix() {
        return (ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getConfig().getString("Prefix"))) + " ");
    }

    public boolean invFull(Player p) {
        return p.getInventory().firstEmpty() == -1;
    }

    public void reloadDataStore() {
        dataStore = YamlConfiguration.loadConfiguration(dataStoreFile);
    }

    public void saveDataStore() {
        try {
            dataStore.save(new File(getDataFolder(), "data.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Map.Entry<String, Integer>> getTopRelicFinders() {
        ConfigurationSection playersSection = dataStore.getConfigurationSection("Players");
        if (playersSection == null) return Collections.emptyList();
        Map<String, Integer> playerRelicCounts = new HashMap<>();

        // Retrieve relic counts for each player, ignoring players with zero relics
        for (String playerUUID : playersSection.getKeys(false)) {
            int relicCount = dataStore.getInt("Players." + playerUUID);
            if (relicCount > 0) {
                playerRelicCounts.put(playerUUID, relicCount);
            }
        }

        // Sort players by relic count in descending order
        List<Map.Entry<String, Integer>> sortedPlayers = new ArrayList<>(playerRelicCounts.entrySet());
        sortedPlayers.sort((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()));

        // Return the top 5 players, or all players if fewer than 5
        return sortedPlayers.size() > 5 ? sortedPlayers.subList(0, 5) : sortedPlayers;
    }

    public boolean playerHasRelicItem(Player player) {
        boolean hasRelic = false;
        for (ItemStack itemInInv : player.getInventory().getContents()) {
            if (itemInInv == null) { continue; }
            boolean isRelic = NBT.get(itemInInv, nbt -> {
                return nbt.getBoolean("RE_IsRelic");
            });
            if (isRelic) { hasRelic = true; }
        }
        return hasRelic;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public void setAssassin(UUID victimUUID) {
        Player victim = getServer().getPlayer(victimUUID);
        if (victim == null || !victim.isOnline()) return;
        List<Player> onlinePlayers = (List<Player>) getServer().getOnlinePlayers()
                .stream()
                .filter(p -> !p.getUniqueId().equals(victimUUID))
                .toList();
        if (onlinePlayers.isEmpty()) return;
        Player assassin = onlinePlayers.get(ThreadLocalRandom.current().nextInt(onlinePlayers.size()));
        bloodforged.put(victimUUID, assassin.getUniqueId());
        assassin.sendMessage(getPrefix() + ChatColor.RED + "You've been chosen to hunt " + ChatColor.BOLD + victim.getName() + ChatColor.RESET + ChatColor.RED + "!");
        assassin.sendMessage(ChatColor.GRAY + "Last known location: " + ChatColor.WHITE + victim.getLocation().getBlockX()
                + ", " + victim.getLocation().getBlockY()
                + ", " + victim.getLocation().getBlockZ());
        assassin.playSound(assassin.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1.0f, 1.0f);
    }

}

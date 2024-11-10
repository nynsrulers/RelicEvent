package com.aelithron.relicevent;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class RelicListener implements Listener {
    private RelicEvent plugin;

    public RelicListener(RelicEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent e) {
        UUID playerUUID = e.getPlayer().getUniqueId();
        if (!plugin.dataStore.isSet("Players." + playerUUID)) {
            plugin.dataStore.set("Players." + playerUUID, 0);
            plugin.saveDataStore();
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Block clicked = e.getClickedBlock();
        if (clicked == null || clicked.getType() != Material.GOLD_BLOCK) {
            return;
        }

        Player player = e.getPlayer();
        Location clickedLoc = clicked.getLocation();

        ConfigurationSection redeemChecking = plugin.getConfig().getConfigurationSection("RedeemBlockLocation");
        if (Objects.equals(redeemChecking.getString("world"), clickedLoc.getWorld().getName()) &&
                redeemChecking.getInt("x") == clickedLoc.getBlockX() &&
                redeemChecking.getInt("y") == clickedLoc.getBlockY() &&
                redeemChecking.getInt("z") == clickedLoc.getBlockZ()) {

            e.setCancelled(true);
            if (!plugin.getConfig().getBoolean("EventEnabled")) {
                player.sendMessage(plugin.getPrefix() + ChatColor.RED + "The event is currently disabled, sorry!");
                return;
            }
            boolean hasRelic = false;
            for (ItemStack itemInInv : player.getInventory().getContents()) {
                if (itemInInv == null || itemInInv.getAmount() == 0 || itemInInv.getType() == Material.AIR) { continue; }
                boolean isRelic = NBT.get(itemInInv, nbt -> {
                    return nbt.getBoolean("RE_IsRelic");
                });
                if (isRelic) {
                    hasRelic = true;
                    String obtainer = NBT.get(itemInInv, nbt -> {
                        return nbt.getString("RE_RelicObtainer");
                    });
                    String relicID = NBT.get(itemInInv, nbt -> {
                        return nbt.getString("RE_RelicID");
                    });
                    if (obtainer == null) {
                        player.sendMessage(plugin.getPrefix() + ChatColor.RED + "This relic redemption has encountered an error. Please message staff immediately! If you are staff, check console for more info.");
                        plugin.getLogger().warning("Player " + player.getName() + " attempted to redeem a relic. This had a RE_IsRelic tag set to true, but an RE_RelicObtainer of null. This is unintended!");
                        return;
                    }
                    if (!obtainer.equals(player.getUniqueId().toString()) && plugin.getConfig().getBoolean("EnforceNoTrade")) {
                        player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You did not obtain this relic, so you may not redeem it.");
                        return;
                    }
                    if (relicID == null) {
                        player.sendMessage(plugin.getPrefix() + ChatColor.RED + "This relic redemption has encountered an error. Please message staff immediately! If you are staff, check console for more info.");
                        plugin.getLogger().warning("Player " + player.getName() + " attempted to redeem a relic. This had a RE_IsRelic tag set to true, but an RE_RelicID of null. This is unintended!");
                        return;
                    }
                    plugin.reloadDataStore();
                    if (!Objects.equals(relicID, "admin")) {
                        if (plugin.dataStore.getBoolean("Relics." + relicID + ".redeemed")) {
                            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "This relic has already been redeemed!");
                            plugin.getLogger().warning("Player " + player.getName() + " attempted to redeem a relic tied to an already redeemed relic ID. This should not be possible, and may even be a dupe glitch!");
                            return;
                        }
                        plugin.dataStore.set("Relics." + relicID + ".redeemed", true);
                        plugin.saveDataStore();
                    }
                    player.getInventory().remove(itemInInv);
                    break;
                }
            }
            if (hasRelic) {
                plugin.reloadDataStore();
                int playerRelicCount = (plugin.dataStore.getInt("Players." + player.getUniqueId()) + 1);
                plugin.dataStore.set("Players." + player.getUniqueId(), playerRelicCount);
                plugin.saveDataStore();
                for (Player everyone : plugin.getServer().getOnlinePlayers()) {
                    everyone.sendMessage(plugin.getPrefix() + ChatColor.GOLD + player.getName() + " has redeemed a Relic! Their total: " + playerRelicCount + " Relics");
                }
                new WebhookNotifier(plugin).sendClaimNotification(player);
                player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "You redeemed a Relic successfully!");
            } else {
                player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You have no relics in your inventory, go out and find some!");
            }
            return;
        }

        ConfigurationSection relicsSection = plugin.dataStore.getConfigurationSection("Relics");
        if (relicsSection == null) {
            return;
        }

        for (String relicID : relicsSection.getKeys(false)) {
            String checkPath = ("Relics." + relicID + ".");

            // Check if the relic location matches
            if (Objects.equals(plugin.dataStore.getString(checkPath + "world"), clickedLoc.getWorld().getName()) &&
                    plugin.dataStore.getInt(checkPath + "x") == clickedLoc.getBlockX() &&
                    plugin.dataStore.getInt(checkPath + "y") == clickedLoc.getBlockY() &&
                    plugin.dataStore.getInt(checkPath + "z") == clickedLoc.getBlockZ()) {

                e.setCancelled(true);
                if (!plugin.getConfig().getBoolean("EventEnabled")) {
                    player.sendMessage(plugin.getPrefix() + ChatColor.RED + "The event is currently disabled, sorry!");
                    return;
                }
                if (plugin.playerHasRelicItem(player) && plugin.getConfig().getBoolean("EnforceNoTrade")) {
                    player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Your relic claim failed, as you already have a relic in your inventory.");
                    player.sendMessage(ChatColor.AQUA + "Please redeem your first relic at spawn before collecting any more.");
                    break;
                }
                if (plugin.invFull(player)) {
                    player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Your relic claim failed, as your inventory is full.");
                    player.sendMessage(ChatColor.AQUA + "Clear up some space and retry.");
                    break;
                }

                for (Entity entity : clicked.getLocation().getChunk().getEntities()) {
                    if (entity.getLocation().distance(clickedLoc) <= 1.5 && entity.getType() == EntityType.ARMOR_STAND) {
                        entity.remove();
                    }
                }
                clicked.setType(Material.AIR);
                plugin.reloadDataStore();
                plugin.dataStore.set("Relics." + relicID + ".found", true);
                plugin.saveDataStore();
                player.getInventory().addItem(ItemCreator.createRelicItem(player, relicID));
                player.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "Congrats, you found a relic!");
                player.sendMessage(ChatColor.GREEN + "Redeem it at spawn for it to count.");

                break;
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        Location brokenLoc = e.getBlock().getLocation();
        ConfigurationSection redeemChecking = plugin.getConfig().getConfigurationSection("RedeemBlockLocation");
        if (Objects.equals(redeemChecking.getString("world"), brokenLoc.getWorld().getName()) &&
                redeemChecking.getInt("x") == brokenLoc.getBlockX() &&
                redeemChecking.getInt("y") == brokenLoc.getBlockY() &&
                redeemChecking.getInt("z") == brokenLoc.getBlockZ()) {

            e.setCancelled(true);
            e.getPlayer().sendMessage(plugin.getPrefix() + ChatColor.RED + "You cannot break the relic redemption block >:3");
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        boolean isRelicBlock = NBT.get(e.getItemInHand(), nbt -> {
            return nbt.getBoolean("RE_IsRelicBlock");
        });
        Player player = e.getPlayer();
        if (!isRelicBlock) { return; }
        Location blockPlaceLoc = e.getBlock().getLocation();
        if (!player.hasPermission("relicevent.admin")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You don't have permission to do this!");
            return;
        }
        ArmorStand hologram = blockPlaceLoc.getWorld().spawn(new Location(blockPlaceLoc.getWorld(),
                blockPlaceLoc.getBlockX() + 0.5F, blockPlaceLoc.getBlockY(), blockPlaceLoc.getBlockZ() + 0.5F), ArmorStand.class);
        hologram.setInvisible(true);
        hologram.setGravity(false);
        hologram.setInvulnerable(true);
        hologram.setCustomNameVisible(true);
        hologram.setCustomName(ChatColor.GOLD + "Relic " + ChatColor.GRAY + "(Click/Tap)");

        UUID relicID = UUID.randomUUID();
        plugin.reloadDataStore();
        plugin.dataStore.set("Relics." + relicID + ".x", blockPlaceLoc.getBlockX());
        plugin.dataStore.set("Relics." + relicID + ".y", blockPlaceLoc.getBlockY());
        plugin.dataStore.set("Relics." + relicID + ".z", blockPlaceLoc.getBlockZ());
        plugin.dataStore.set("Relics." + relicID + ".world", blockPlaceLoc.getWorld().getName());
        plugin.dataStore.set("Relics." + relicID + ".found", false);
        plugin.dataStore.set("Relics." + relicID + ".redeemed", false);
        plugin.saveDataStore();

        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Successfully added a Relic!");
    }
}
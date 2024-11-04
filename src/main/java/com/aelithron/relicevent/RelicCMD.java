package com.aelithron.relicevent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RelicCMD implements CommandExecutor {
    private RelicEvent plugin;
    public RelicCMD (RelicEvent plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "This command can only be used by players.");
        }
        assert sender instanceof Player;
        Player player = (Player) sender;

        if (args.length != 0 && (args[0].equalsIgnoreCase("leaderboard") || args[0].equalsIgnoreCase("lb"))) {
            List<Map.Entry<String, Integer>> topPlayers = plugin.getTopRelicFinders();
            player.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "Relic Leaderboard");
            int rank = 1;
            for (Map.Entry<String, Integer> entry : topPlayers) {
                String playerName = Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey())).getName();
                int relicCount = entry.getValue();
                player.sendMessage(ChatColor.YELLOW.toString() + rank + ". " + ChatColor.GREEN + playerName + " - " + ChatColor.AQUA + relicCount + " relics");
                rank++;
            }
        } else {
            int relicCount = plugin.dataStore.getInt("Players." + player.getUniqueId());
            player.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "Relics found by " + player.getName() + ": " + ChatColor.BOLD + relicCount);
            player.sendMessage(ChatColor.AQUA + "To get the leaderboard, run" + ChatColor.BOLD + " /relic lb" + ChatColor.AQUA + ".");
        }
        return false;
    }
}

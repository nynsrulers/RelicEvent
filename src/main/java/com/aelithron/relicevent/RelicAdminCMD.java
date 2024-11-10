package com.aelithron.relicevent;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RelicAdminCMD implements CommandExecutor {
    private RelicEvent plugin;
    public RelicAdminCMD(RelicEvent plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("relicevent.admin")) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You don't have permission to do this!");
            return false;
        }
        if (args.length == 0) {
            sendHelpMessage(sender);
            return false;
        }
        if (args[0].equalsIgnoreCase("status")) {
            if (args.length < 2) {
                sendHelpMessage(sender);
                return false;
            }
            if (args[1].equalsIgnoreCase("on")) {
                plugin.reloadConfig();
                plugin.getConfig().set("EventEnabled", true);
                plugin.saveConfig();
                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Done! The event is now enabled :3");
                return true;
            } else if (args[1].equalsIgnoreCase("off")) {
                plugin.reloadConfig();
                plugin.getConfig().set("EventEnabled", false);
                plugin.saveConfig();
                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Done! The event is now disabled :3");
                return true;
            } else {
                sendHelpMessage(sender);
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("giveblock")) {
            if (!(sender instanceof Player)) {
                sendHelpMessage(sender);
                return false;
            }
            Player player = (Player) sender;
            if (plugin.invFull(player)) {
                player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Your inventory is full >:3");
                return false;
            }
            player.getInventory().addItem(ItemCreator.createRelicBlock());
            player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "a cat delivered you a relic block!");
            return true;
        }
        if (args[0].equalsIgnoreCase("giveitem")) {
            if (!(sender instanceof Player) || args.length < 2) {
                sendHelpMessage(sender);
                return false;
            }
            Player player = (Player) sender;
            Player finder = plugin.getServer().getPlayer(args[1]);
            if (finder == null || !finder.isOnline()) {
                player.sendMessage(plugin.getPrefix() + ChatColor.RED + "The 'finder' provided could not be found, are they online?");
                return false;
            }
            if (plugin.invFull(player)) {
                player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Your inventory is full >:3");
                return false;
            }
            player.getInventory().addItem(ItemCreator.createRelicItem(finder, "admin"));
            player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "a cat delivered you a relic item (finder: " + finder.getName() + ") :3");
            return true;
        }
        if (args[0].equalsIgnoreCase("setscore")) {
            if (args.length < 3) {
                sendHelpMessage(sender);
                return false;
            }
            Player affected = plugin.getServer().getPlayer(args[1]);
            if (affected == null) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "The player provided could not be found.");
                return false;
            }
            plugin.reloadDataStore();
            plugin.dataStore.set("Players." + affected.getUniqueId(), Integer.parseInt(args[2]));
            plugin.saveDataStore();
            sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Player " + affected.getName() + "'s score is now " + args[2] + " relics.");
            return true;
        }
        if (args[0].equalsIgnoreCase("getscore")) {
            if (args.length < 2) {
                sendHelpMessage(sender);
                return false;
            }
            Player affected = plugin.getServer().getPlayer(args[1]);
            if (affected == null) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "The player provided could not be found.");
                return false;
            }
            plugin.reloadDataStore();
            int score = plugin.dataStore.getInt("Players." + affected.getUniqueId());
            sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Player " + affected.getName() + "'s score: " + score + " relics.");
            return true;
        }
        if (args[0].equalsIgnoreCase("testdiscord")) {
            if (plugin.getConfig().getString("DiscordWebhook") == null || plugin.getConfig().getString("DiscordWebhook").equals("CHANGEME")) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "The config doesn't have a webhook URL set!");
                return false;
            }
            new WebhookNotifier(plugin).sendTestPing();
            sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "webhook test sent :3");
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadDataStore();
            plugin.reloadConfig();
            sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "reload done! *happy purring*");
            return true;
        }
        if (args[0].equalsIgnoreCase("dump")) {
            new RelicDumper(plugin).dumpRelics();
            sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "done dumping the relics!!! :3 (check console)");
            return true;
        }
        if (args[0].equalsIgnoreCase("setting")) {
            if (args.length < 3) {
                sendHelpMessage(sender);
                return false;
            }
            boolean state;
            if (args[2].equalsIgnoreCase("on")) { state = true; }
            else if (args[2].equalsIgnoreCase("off")) { state = false; }
            else {
                sendHelpMessage(sender);
                return false;
            }
            if (args[1].equalsIgnoreCase("notrade") || args[1].equalsIgnoreCase("EnforceNoTrade")) {
                plugin.reloadConfig();
                plugin.getConfig().set("EnforceNoTrade", state);
                plugin.saveConfig();
                return true;
            }
            if (args[1].equalsIgnoreCase("holdinglimit") || args[1].equalsIgnoreCase("EnforceHoldingLimit")) {
                plugin.reloadConfig();
                plugin.getConfig().set("EnforceHoldingLimit", state);
                plugin.saveConfig();
                return true;
            }
            sendHelpMessage(sender);
            return false;
        }
        sendHelpMessage(sender);
        return false;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Incorrect usage! Command help:");
        sender.sendMessage(ChatColor.AQUA + "/relicmgr status (on|off): Change the event's status (on or off).");
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.AQUA + "/relicmgr giveblock: Generates a block that can be used to create relics.");
            sender.sendMessage(ChatColor.AQUA + "/relicmgr giveitem (finder): Generates a relic item. Finder must be online, item will be given to you.");
            sender.sendMessage(ChatColor.GRAY + "Note for giveitem: Only the listed finder can redeem the item.");
        }
        sender.sendMessage(ChatColor.AQUA + "/relicmgr setscore (player) (score): Sets the player's relic score (found relic count).");
        sender.sendMessage(ChatColor.AQUA + "/relicmgr getscore (player): Look up a player's current score.");
        sender.sendMessage(ChatColor.AQUA + "/relicmgr dump: Dumps all relics into a text file, split into found and not found.");
        sender.sendMessage(ChatColor.AQUA + "/relicmgr testdiscord: Send a test through the Discord webhook.");
        sender.sendMessage(ChatColor.AQUA + "/relicmgr reload: Reloads the plugin and all configs.");
        sender.sendMessage(ChatColor.AQUA + "/relicmgr setting (setting) (on|off): Change a game setting.");
        sender.sendMessage(ChatColor.GRAY + "Supported settings are notrade (can't redeem others' relics) and holdinglimit (can't hold more than one relic at once).");
    }
}
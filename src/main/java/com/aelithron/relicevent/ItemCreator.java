package com.aelithron.relicevent;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemCreator {
    public static ItemStack createRelicItem(Player finder, String relicID) {
        ItemStack relic = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta relicMeta = relic.getItemMeta();
        assert relicMeta != null;

        relicMeta.setItemName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Relic");
        // Lore
        List<String> relicLore = new ArrayList<>();
        relicLore.add(" "); // spacer
        relicLore.add(ChatColor.GRAY + "A " + ChatColor.LIGHT_PURPLE + "legendary " + ChatColor.GRAY + "item obtained during the Relic Event.");
        relicLore.add(ChatColor.GREEN + "Redeem this for points at spawn.");
        relicLore.add(ChatColor.GOLD + "Obtained by: " + finder.getName());
        relicMeta.setLore(relicLore);
        // Glow :)
        relicMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
        relicMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        relic.setItemMeta(relicMeta);
        // NBT Tags
        NBT.modify(relic, nbt -> {
            nbt.setBoolean("RE_IsRelic", true);
            nbt.setString("RE_RelicObtainer", finder.getUniqueId().toString());
            nbt.setString("RE_RelicID", relicID);
        });
        return relic;
    }

    public static ItemStack createRelicBlock() {
        ItemStack relicBlock = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta relicMeta = relicBlock.getItemMeta();
        assert relicMeta != null;

        relicMeta.setItemName(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "ADMIN" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD + ChatColor.BOLD + "RELIC BLOCK");
        // Lore
        List<String> relicLore = new ArrayList<>();
        relicLore.add(" "); // spacer
        relicLore.add(ChatColor.GRAY + "Admin-only item to create a new relic.");
        relicMeta.setLore(relicLore);
        // Glow :)
        relicMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
        relicMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        relicBlock.setItemMeta(relicMeta);
        NBT.modify(relicBlock, nbt -> {
            nbt.setBoolean("RE_IsRelicBlock", true);
        });
        return relicBlock;
    }
}

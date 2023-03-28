package me.mynqme.pickaxelock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class InventoryManager {
    public static void openMainMenu(Player player) {
        InvHolder invholder = new InvHolder();
        String title = ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &f&lPickaxeLock");
        Inventory inv = Bukkit.createInventory(invholder, 9, title);
        ArrayList<String> lore = new ArrayList<>();
        // create a new list for lore
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Click to change your pickaxe to this slot!"));
        for (int i = 0; i < 9; i++) {
            createDisplay(new ItemStack(Objects.requireNonNull(Material.getMaterial("STAINED_GLASS_PANE")), 1, (short) 0), true, inv, i, ChatColor.translateAlternateColorCodes('&', "&5&lSlot " + i), lore);
        }

        player.openInventory(inv);
    }

    public static void createDisplay(ItemStack item, boolean enchant, Inventory inv, int slot, String name, @NotNull ArrayList<String> lore) {
        // for each string in the lore variable, make this a component
        ItemMeta meta = item.getItemMeta();
        if (meta != null && name != null) {
            meta.setDisplayName(name);
            if (enchant) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            meta.setLore(lore);
        }

        item.setItemMeta(meta);
        item.setAmount(1);
        inv.setItem(slot, item);
    }
}

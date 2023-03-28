package me.mynqme.pickaxelock;

import org.bukkit.ChatColor;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use that command!");
            return true;
        }
        if (command.getName().equalsIgnoreCase("pickaxelock") && args.length == 0) {

            Player player = (Player)sender;
            if (player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_PICKAXE)) {
                InventoryManager.openMainMenu(player);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Config.prefix + "&cYou must be holding a diamond pickaxe to use this command"));
            }
            return true;
        }

        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("reload") && command.getName().equalsIgnoreCase("pickaxelock")) {
                Player player = (Player)sender;
                if (player.hasPermission("pickaxelock.reload")) {
                    Files.base();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Config.prefix + "&aReloaded"));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Config.prefix + "&fUnknown Command"));
                }
            }
        }

        return true;
    }
}

package dev.tom974.pickaxelock;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.*;

public class PickaxeEvents implements Listener {

    public HashMap<UUID, ItemStack> pickaxe = new HashMap<>();
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("pickaxelock.bypass")) return;
        try {
            Database.getPlayer(event.getPlayer().getUniqueId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("pickaxelock.bypass")) return;
        if (Objects.requireNonNull(event.getMainHandItem()).getType() == Material.DIAMOND_PICKAXE || Objects.requireNonNull(event.getOffHandItem()).getType() == Material.DIAMOND_PICKAXE) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Config.prefix + Config.lockMessage));
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (event.getPlayer().hasPermission("pickaxelock.bypass")) return;
        Database.savePlayer(event.getPlayer().getUniqueId(), PickaxeLock.PickaxeData.get(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Collection<ItemStack> drops = new ArrayList<>(event.getDrops());
        boolean found = false;
        ItemStack items = null;
        for (ItemStack item : drops) {
            if (item.getType() == Material.DIAMOND_PICKAXE) {
                found = true;
                items = item;
            }
        }

        if (found) {
            event.getDrops().remove(items);
            pickaxe.put(player.getUniqueId(), items);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        // prevent lost of pickaxe during respawn event
        Player player = event.getPlayer();
        if (player.hasPermission("pickaxelock.bypass")) return;
        if (pickaxe.containsKey(player.getUniqueId())) {
            player.getInventory().setItem(PickaxeLock.PickaxeData.get(player.getUniqueId()), pickaxe.get(player.getUniqueId()));
        }
    }

    @EventHandler
    public void on(InventoryMoveItemEvent event) {
        // get playerwil
        Player player = (Player)event.getDestination().getHolder();
        assert player != null;
        if (player.hasPermission("pickaxelock.bypass")) return;
        if (event.getItem().getType().equals(Material.DIAMOND_PICKAXE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // get the player that clicked in inventory
        Player player = (Player)event.getWhoClicked();
        if (event.getInventory().getHolder() instanceof InvHolder && event.getRawSlot() < 9 && !String.valueOf(event.getRawSlot()).equals("-999")) {
            event.setCancelled(true);
            // get item in slot
            ItemStack pickaxe_check = event.getWhoClicked().getInventory().getItem(PickaxeLock.PickaxeData.get(player.getUniqueId()));
            if (pickaxe_check == null || !pickaxe_check.getType().equals(Material.DIAMOND_PICKAXE)) {
                player.sendMessage("Pickaxe is not in the assigned slot. Please report this to staff immediately.");
                return;
            }

            ItemStack newSlot_check = player.getInventory().getItem(event.getRawSlot());
            if (newSlot_check != null) {
                if (!newSlot_check.getType().equals(Material.AIR)) {
                    player.sendMessage("The selected slot is not empty. please select a empty slot!");
                    return;
                }
            }
           
            if (!PickaxeLock.PickaxeData.containsKey(player.getUniqueId())) {
                player.sendMessage("Data is null. Please report this to staff immediately.");
                return;
            }
            player.getInventory().setItem(PickaxeLock.PickaxeData.get(player.getUniqueId()), null); // set old slot to null so we dont dupe XD
            player.getInventory().setItem(event.getRawSlot(), pickaxe_check);
            PickaxeLock.PickaxeData.put(player.getUniqueId(), event.getRawSlot()); // set new slot in hashmap
            player.closeInventory();
        }

        if (event.getClick().equals(ClickType.NUMBER_KEY)) {
            if (event.getWhoClicked().hasPermission("pickaxelock.bypass")) return;
            int slot = event.getHotbarButton();
            if (slot >= 0 && slot <= 8) {
                if (Objects.requireNonNull(event.getWhoClicked().getInventory().getItem(PickaxeLock.PickaxeData.get(player.getUniqueId()))).getType().equals(Material.DIAMOND_PICKAXE)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Config.prefix + Config.lockMessage));
                }
            }

        }

        ItemStack current = event.getCurrentItem();
        if (current == null) return;
        if (current.getType().equals(Material.DIAMOND_PICKAXE)) {
            if (event.getWhoClicked().hasPermission("pickaxelock.bypass")) return;
            event.setCancelled(true);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Config.prefix + Config.lockMessage));
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("pickaxelock.bypass")) return;
        if (event.getItemDrop().getItemStack().getType() == Material.DIAMOND_PICKAXE) {
            if (!player.hasPermission("ppl.admin")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Config.prefix + Config.lockMessage));
            }
        }
    }
}

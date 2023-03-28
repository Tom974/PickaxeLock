package me.mynqme.pickaxelock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public final class PickaxeLock extends JavaPlugin {

    public static HashMap<UUID, Integer> PickaxeData = new HashMap<>();

    public void onEnable() {
        Files.base();

        try {
            Database.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                Database.getPlayer(p.getUniqueId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        getServer().getPluginManager().registerEvents(new PickaxeEvents(), this);
        Objects.requireNonNull(getCommand("pickaxelock")).setExecutor(new Commands());
    }

    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.BLACK + "[PickaxeLock]: Disabled");

        for (UUID uuid : PickaxeData.keySet()) {
            Database.savePlayer(uuid, PickaxeData.get(uuid));
        }

        try {
            Database.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void console(String msg) {
        Bukkit.getServer().getLogger().info(ChatColor.translateAlternateColorCodes('&', msg));
    }
}

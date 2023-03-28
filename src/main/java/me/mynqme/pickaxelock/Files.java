package me.mynqme.pickaxelock;

import java.io.*;
import org.bukkit.configuration.file.*;

public class Files
{
    public static File configFile;
    public static FileConfiguration config;

    public static void base() {
        final PickaxeLock main = PickaxeLock.getPlugin(PickaxeLock.class);

        // Config file
        Files.configFile = new File(main.getDataFolder(), "config.yml");
        if (!Files.configFile.exists()) {
            main.saveResource("config.yml", false);
        }
        Files.config = YamlConfiguration.loadConfiguration(Files.configFile);

        // Apply configs
        Config.apply();
    }
}

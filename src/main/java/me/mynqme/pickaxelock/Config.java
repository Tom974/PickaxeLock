package me.mynqme.pickaxelock;

import org.bukkit.configuration.*;

public class Config
{
    public static String prefix;
    public static String databaseHost;
    public static String databaseName;
    public static String databaseUser;
    public static Boolean debug;
    public static String databasePassword;
    public static String lockMessage;
    
    public static void apply() {
        ConfigurationSection conf = Files.config.getConfigurationSection("");
        assert conf != null;
        Config.prefix = conf.getString("prefix");
        Config.debug = conf.getBoolean("debug");
        Config.lockMessage = conf.getString("lockMessage");

        ConfigurationSection database = Files.config.getConfigurationSection("database");
        assert database != null;
        Config.databaseHost = database.getString("host");
        Config.databaseName = database.getString("database");
        Config.databaseUser = database.getString("user");
        Config.databasePassword = database.getString("password");
    }
    
    public static void reload() {
        Files.base();
    }
    
    static {
        Config.prefix = "";
        Config.debug = false;
        Config.databaseHost = "";
        Config.databaseName = "";
        Config.databaseUser = "";
        Config.databasePassword = "";
        Config.lockMessage = "";
    }
}

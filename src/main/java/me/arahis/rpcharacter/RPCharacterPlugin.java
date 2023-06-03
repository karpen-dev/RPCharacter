package me.arahis.rpcharacter;

import me.arahis.rpcharacter.database.DatabaseHandler;
import me.arahis.rpcharacter.listeners.JoinListener;
import net.skinsrestorer.api.SkinsRestorerAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RPCharacterPlugin extends JavaPlugin {

    private static RPCharacterPlugin plugin;
    private SkinsRestorerAPI skinsRestorerAPI;
    private DatabaseHandler databaseHandler;
    @Override
    public void onEnable() {

        plugin = this;
        databaseHandler = new DatabaseHandler();

        //in case of that my plugin will start earlier than SkinRestorer
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if(SkinsRestorerAPI.getApi() != null) {
                skinsRestorerAPI = SkinsRestorerAPI.getApi();
                System.out.println("SkinRestorerAPI has been successfully initialized");
            }
        }, 100L);

        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);

        databaseHandler.initTables();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }

    public static RPCharacterPlugin getPlugin() {
        return plugin;
    }

    public SkinsRestorerAPI getSkinsRestorerAPI() {
        return skinsRestorerAPI;
    }
}

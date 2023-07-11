package me.arahis.rpcharacter;

import me.arahis.rpcharacter.commands.*;
import me.arahis.rpcharacter.commands.tabcompleters.CharSetRoleTabCompleter;
import me.arahis.rpcharacter.commands.tabcompleters.CharSetSkinTabCompleter;
import me.arahis.rpcharacter.commands.tabcompleters.RPCharTabCompleter;
import me.arahis.rpcharacter.database.IDataHandler;
import me.arahis.rpcharacter.database.JSONDataHandler;
import me.arahis.rpcharacter.database.MySQLDataHandler;
import me.arahis.rpcharacter.listeners.JoinListener;
import me.arahis.rpcharacter.placeholders.RPCharacterExpansion;
import me.arahis.rpcharacter.utils.CharCreationUtils;
import me.arahis.rpcharacter.utils.Refactor;
import me.kodysimpson.simpapi.menu.MenuManager;
import net.skinsrestorer.api.SkinsRestorerAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class RPCharacterPlugin extends JavaPlugin {

    private static RPCharacterPlugin plugin;
    private SkinsRestorerAPI skinsRestorerAPI;
    private IDataHandler dataHandler;
    private CharCreationUtils charCreationUtils;

    @Override
    public void onEnable() {

        plugin = this;
        charCreationUtils = new CharCreationUtils();

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        switch (getConfig().getString("database-type")) {

            case "MySQL":

                Refactor.sendInfo("Data storage method connected!");
                Refactor.sendInfo("MySQL selected!");

                dataHandler = new MySQLDataHandler();
                ((MySQLDataHandler) dataHandler).initTables();

                break;
            case "JSON":

                Refactor.sendInfo("Data storage method connected!");
                Refactor.sendInfo("JSON selected!");

                dataHandler = new JSONDataHandler();

                getCommand("savedata").setExecutor(new SaveDataCommand());

                try {

                    ((JSONDataHandler) dataHandler).loadCharacters();
                    ((JSONDataHandler) dataHandler).loadRPPlayers();

                    Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                        try {
                            ((JSONDataHandler) dataHandler).saveCharacters();
                            ((JSONDataHandler) dataHandler).saveRPPlayers();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }, 100L, 20 * 60 * 5L);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

        }

        MenuManager.setup(getServer(), this);

        // in case if my plugin will start earlier than SkinsRestorer
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if(SkinsRestorerAPI.getApi() != null) {
                skinsRestorerAPI = SkinsRestorerAPI.getApi();
                Refactor.sendInfo("SkinsRestorerAPI has been successfully initialized");
            }
        }, 50L);

        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new RPCharacterExpansion().register();
            }
        }, 50L);

        getCommand("charsetskin").setTabCompleter(new CharSetSkinTabCompleter());
        getCommand("charsetrole").setTabCompleter(new CharSetRoleTabCompleter());
        getCommand("rpchar").setTabCompleter(new RPCharTabCompleter());

        getCommand("createchar").setExecutor(new CreateCharCommand());
        getCommand("charsetskin").setExecutor(new CharSetSkinCommand());
        getCommand("charsetrole").setExecutor(new CharSetRoleCommand());
        getCommand("charsmenu").setExecutor(new CharsMenuCommand());
        getCommand("rpchar").setExecutor(new RPCharCommand());


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public CharCreationUtils getCharCreationUtils() {
        return charCreationUtils;
    }
    public IDataHandler getDataHandler() {
        return dataHandler;
    }

    public static RPCharacterPlugin getPlugin() {
        return plugin;
    }

    public SkinsRestorerAPI getSkinsRestorerAPI() {
        return skinsRestorerAPI;
    }
}

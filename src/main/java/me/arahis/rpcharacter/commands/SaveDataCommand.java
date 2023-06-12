package me.arahis.rpcharacter.commands;

import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.database.IDataHandler;
import me.arahis.rpcharacter.database.JSONDataHandler;
import me.arahis.rpcharacter.utils.Refactor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SaveDataCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        RPCharacterPlugin plugin = RPCharacterPlugin.getPlugin();
        IDataHandler handler = plugin.getDataHandler();

        try {
            ((JSONDataHandler) handler).saveRPPlayers();
            ((JSONDataHandler) handler).saveCharacters();
            Refactor.sendMessage(sender, "Инфомарция сохранена!");
        } catch (IOException e) {
            Refactor.sendMessage(sender, "Ошибка!");
            e.printStackTrace();
        }

        return true;
    }
}

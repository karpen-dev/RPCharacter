package me.arahis.rpcharacter.commands;

import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.utils.Refactor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RPCharCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args[0].equals("reload")) {
            RPCharacterPlugin.getPlugin().reloadConfig();
            RPCharacterPlugin.getPlugin().saveConfig();
            Refactor.sendMessage(sender, "Конфиг перезагружен");
        }

        return true;
    }
}

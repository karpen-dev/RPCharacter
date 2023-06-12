package me.arahis.rpcharacter.commands;

import me.arahis.rpcharacter.menu.CharacterSelectionMenu;
import me.arahis.rpcharacter.utils.Refactor;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.MenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CharsMenuCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        try {
            MenuManager.openMenu(CharacterSelectionMenu.class, player);
        } catch (MenuManagerException | MenuManagerNotSetupException e) {
            Refactor.sendMessageFromConfig(player, "menu-exception");
            e.printStackTrace();
        }

        return true;
    }
}

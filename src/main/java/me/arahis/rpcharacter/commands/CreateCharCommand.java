package me.arahis.rpcharacter.commands;

import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.database.DatabaseHandler;
import me.arahis.rpcharacter.utils.Refactor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreateCharCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)) {
            Refactor.sendMessageFromConfig(sender, "only-for-players");
            return true;
        }

        if(args.length < 2) {
            Refactor.sendMessageFromConfig(sender, "wrong-usage");
            return true;
        }

        RPCharacterPlugin plugin = RPCharacterPlugin.getPlugin();
        DatabaseHandler handler = plugin.getDatabaseHandler();
        int limit = plugin.getConfig().getInt("limit");

        Player player = (Player) sender;

        if(handler.getRPPlayer(player).getAmountOfChars() > limit) {
            Refactor.sendMessageFromConfig(player, "limit-of-characters");
            return true;
        }

        // /createchar <имя> <роль>

        String name = args[0];


        return true;
    }
}

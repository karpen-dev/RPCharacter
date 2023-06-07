package me.arahis.rpcharacter.commands;

import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.database.DatabaseHandler;
import me.arahis.rpcharacter.models.Character;
import me.arahis.rpcharacter.utils.Refactor;
import net.skinsrestorer.api.property.IProperty;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

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

        // /createchar <роль> <имя>

        String role = args[0];
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String name = sb.toString().trim();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            IProperty property = plugin.getSkinsRestorerAPI().getSkinData(player.getName());

            Character character = new Character(
                    (long) (handler.getLastCharId() + 1),
                    player.getName(),
                    player.getUniqueId().toString(),
                    role,
                    name,
                    property.getName(),
                    property.getValue(),
                    property.getSignature(),
                    handler.getLastCharIdByPlayer(player) + 1);
            try {
                handler.createChar(character);
            } catch (SQLException e) {
                Refactor.sendFormattedWarn("%s's character %s wasn't saved successfully", character.getOwnerName(), character.getCharName());
                Refactor.sendMessage(player, "Ошибка подключения к базе данных! Создайте тикет в поддержке");
                e.printStackTrace();
                return;
            }


            // Вы создали персонажа [%s] %s с номером %d
            Refactor.sendMessage(player, String.format(plugin.getConfig().getString("char-created"), role, name, character.getCharId()));
        });

        return true;
    }
}

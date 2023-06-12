package me.arahis.rpcharacter.commands;

import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.database.IDataHandler;
import me.arahis.rpcharacter.database.SavingType;
import me.arahis.rpcharacter.models.Character;
import me.arahis.rpcharacter.models.RPPlayer;
import me.arahis.rpcharacter.utils.Refactor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class CharSetRoleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)) {
            Refactor.sendMessageFromConfig(sender, "only-for-players");
            return true;
        }

        Player player = (Player) sender;

        if(args.length < 2) {
            Refactor.sendMessageFromConfig(sender, "wrong-usage");
            return true;
        }

        RPCharacterPlugin plugin = RPCharacterPlugin.getPlugin();
        IDataHandler handler = plugin.getDataHandler();

        if(Refactor.isNotANumber(args[0])) {
            Refactor.sendMessageFromConfig(player, "id-should-be-number");
            return true;
        }

        // /charsetrole <id> [role]
        int id = Integer.parseInt(args[0]);
        if(id == 1) {
            Refactor.sendMessage(player, "Нельзя редактировать Нон-РП персонажа");
            return true;
        }
        String role = Refactor.stringBuilder(args, 1);

        if(role.length() > 32) {
            Refactor.sendMessage(player, "Роль должна быть меньше 32 символов");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            Character character = handler.getCharacter(player, id);
            RPPlayer rpPlayer = handler.getRPPlayer(player);
            if(character == null) {
                // Персонаж с номером %d не найден!
                Refactor.sendMessage(player, String.format(plugin.getConfig().getString("char-not-found"), id));
                return;
            }

            character.setCharRole(role);

            try {
                handler.saveCharacter(character, SavingType.UPDATE);
            } catch (SQLException e) {
                Refactor.sendFormattedWarn("%s's character #%d %s wasn't successfully updated", character.getOwnerName(), character.getCharId(), character.getOwnerName());
                Refactor.sendMessageFromConfig(player, "db-con-error");
                return;
            }

            Refactor.sendFormattedInfo("Role updated to %s", role);

            if(rpPlayer.getSelectedChar() == id) {
                Refactor.setDisplayName(player, character);
            }

            // Роль персонажа #%d %s была изменена на %s
            Refactor.sendMessage(player, String.format(plugin.getConfig().getString("role-updated"), id, character.getCharName(), character.getCharRole()));

        });

        return true;
    }
}

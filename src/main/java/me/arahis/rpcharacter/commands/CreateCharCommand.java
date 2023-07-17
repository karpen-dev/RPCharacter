package me.arahis.rpcharacter.commands;

import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.database.IDataHandler;
import me.arahis.rpcharacter.database.SavingType;
import me.arahis.rpcharacter.models.Character;
import me.arahis.rpcharacter.models.RPPlayer;
import me.arahis.rpcharacter.utils.Refactor;
import net.skinsrestorer.api.SkinVariant;
import net.skinsrestorer.api.exception.SkinRequestException;
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

        RPCharacterPlugin plugin = RPCharacterPlugin.getPlugin();

        if(args.length < 2) {
            Refactor.sendMessage(sender, String.format(plugin.getConfig().getString("wrong-usage"), command.getUsage()));
            return true;
        }

        IDataHandler handler = plugin.getDataHandler();
        int limit = plugin.getConfig().getInt("limit");

        Player player = (Player) sender;

        // /createchar <роль> <имя>

        String role = args[0];
        String name = Refactor.stringBuilder(args, 1);

        if(role.length() > 32) {
            Refactor.sendMessage(player, "Роль должна быть меньше 32 символов");
            return true;
        }

        if(name.length() > 32) {
            Refactor.sendMessage(player, "Имя должно быть меньше 32 символов");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if(handler.getRPPlayer(player).getAmountOfChars() >= limit) {
                Refactor.sendMessageFromConfig(player, "limit-of-characters");
                return;
            }

            IProperty property = plugin.getSkinsRestorerAPI().getSkinData(player.getName());

            if(property == null) {
                try {
                    property = plugin.getSkinsRestorerAPI().genSkinUrl("https://ic.wampi.ru/2023/06/17/Original_Steve_with_Beard.png", SkinVariant.CLASSIC);
                } catch (SkinRequestException e) {
                    Refactor.sendMessage(player, "Ошибка SkinsRestorerAPI! Попробуйте снова позже!");
                    Refactor.sendMessage(player, "Если ошибка осталась, создайте тикет в поддержке!");
                    e.printStackTrace();
                    return;
                }
            }

            RPPlayer rpPlayer = handler.getRPPlayer(player);
            rpPlayer.setAmountOfChars(rpPlayer.getAmountOfChars() + 1);

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
                handler.saveCharacter(character, SavingType.SAVE);
                handler.saveRPPlayer(rpPlayer, SavingType.UPDATE);
            } catch (SQLException e) {
                Refactor.sendFormattedWarn("%s's character %s wasn't saved successfully", character.getOwnerName(), character.getCharName());
                Refactor.sendMessageFromConfig(player, "db-con-error");
                e.printStackTrace();
                return;
            }


            // Вы создали персонажа [%s] %s с номером %d
            Refactor.sendMessage(player, String.format(plugin.getConfig().getString("char-created"), character.getCharId(), role, name));
        });

        return true;
    }
}

package me.arahis.rpcharacter.listeners;

import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.database.IDataHandler;
import me.arahis.rpcharacter.database.SavingType;
import me.arahis.rpcharacter.models.Character;
import me.arahis.rpcharacter.models.RPPlayer;
import me.arahis.rpcharacter.utils.Refactor;
import net.skinsrestorer.api.property.IProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        RPCharacterPlugin plugin = RPCharacterPlugin.getPlugin();
        IDataHandler handler = plugin.getDataHandler();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (handler.getRPPlayer(player) == null) {
                try {
                    handler.saveRPPlayer(new RPPlayer(player.getUniqueId().toString(), player.getName(), 1, 1), SavingType.SAVE);
                } catch (SQLException e) {
                    Refactor.sendFormattedWarn("RPPlayer %s wasn't saved successfully", player.getName());
                    e.printStackTrace();
                }
            }

            IProperty property = plugin.getSkinsRestorerAPI().getSkinData(player.getName());

            if(handler.getCharacter(player, 1) == null) {
                try {
                    handler.saveCharacter(new Character(
                            (long) handler.getLastCharId() + 1,
                            player.getName(),
                            player.getUniqueId().toString(),
                            "Нон-РП",
                            player.getName(),
                            property.getName(),
                            property.getValue(),
                            property.getSignature(),
                            1
                    ), SavingType.SAVE);
                } catch (SQLException e) {
                    Refactor.sendFormattedWarn("%s's character #%d %s wasn't successfully saved", player.getName(), 1, player.getName());
                    e.printStackTrace();
                }
            }

        });

    }
}

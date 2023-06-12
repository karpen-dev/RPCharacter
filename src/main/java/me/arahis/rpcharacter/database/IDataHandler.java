package me.arahis.rpcharacter.database;

import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.models.Character;
import me.arahis.rpcharacter.models.RPPlayer;
import me.arahis.rpcharacter.utils.Refactor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public interface IDataHandler {


    List<Character> getAllCharacters();
    List<Character> getPlayerCharacters(Player player);
    Character getCharacter(Player player, int id);
    void saveCharacter(Character character, SavingType type) throws SQLException;
    void deleteCharacter(Player player, int id);

    List<RPPlayer> getAllRPPlayers();
    RPPlayer getRPPlayer(Player player);
    void saveRPPlayer(RPPlayer rpPlayer, SavingType type) throws SQLException;
    void deleteRPPlayer(Player player);

    public int getLastCharId();
    public int getLastCharIdByPlayer(Player player);

    public default void replaceCharacter(RPCharacterPlugin plugin, Player p, RPPlayer rpPlayer, IDataHandler handler) {

        Character nonRp = getCharacter(p, 1);
        Refactor.setCharacterToPlayer(p, nonRp);

        rpPlayer.setSelectedChar(1);
        try {
            handler.saveRPPlayer(rpPlayer, SavingType.UPDATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

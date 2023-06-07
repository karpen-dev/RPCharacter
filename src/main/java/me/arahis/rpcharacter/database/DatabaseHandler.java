package me.arahis.rpcharacter.database;

import me.arahis.rpcharacter.models.Character;
import me.arahis.rpcharacter.models.RPPlayer;
import me.arahis.rpcharacter.utils.Refactor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHandler {

    public void initTables() {
        try (Connection connection = DataSource.getConnection()) {

            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS characters(id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, owner_name varchar(25), owner_uuid varchar(36), char_role varchar(32), char_name varchar(32), property_name varchar(1500), property_value varchar(1500), property_signature varchar(1500), char_id INT)");
            PreparedStatement statement1 = connection.prepareStatement("CREATE TABLE IF NOT EXISTS rpplayers(uuid varchar(36) NOT NULL PRIMARY KEY, name varchar(16), amount_of_chars int, selected_char int)");

            try {
                statement.execute();
                Refactor.sendInfo("Table characters was created");
            } catch (SQLException ex) {
                Refactor.sendWarn("Error while table characters creation");
                ex.printStackTrace();
            }

            try {
                statement1.execute();
                Refactor.sendInfo("Table rpplayer was created");
            } catch (SQLException ex) {
                Refactor.sendWarn("Error while table rpplayers creation");
                ex.printStackTrace();
            }

            statement.close();
            statement1.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createChar(Character character) throws SQLException{

        try (Connection connection = DataSource.getConnection()) {

            PreparedStatement statement = connection.prepareStatement("INSERT INTO characters(owner_name, owner_uuid, char_role, char_name, property_name, property_value, property_signature, char_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

            statement.setString(1, character.getOwnerName());
            statement.setString(2, character.getOwnerUUID());
            statement.setString(3, character.getCharRole());
            statement.setString(4, character.getCharName());
            statement.setString(5, character.getPropertyName());
            statement.setString(6, character.getPropertyValue());
            statement.setString(7, character.getPropertySignature());
            statement.setInt(8, character.getCharId());

            statement.executeUpdate();
            Refactor.sendFormattedInfo("%s's character #%d %s was saved", character.getOwnerName(), character.getCharId(), character.getCharName());

            statement.close();

        }
    }

    public void createRPPlayer(RPPlayer rpPlayer) throws SQLException {

        try (Connection connection = DataSource.getConnection()) {

            PreparedStatement statement = connection.prepareStatement("INSERT INTO rpplayers(uuid, name, amount_of_chars, selected_char) VALUES (?, ?, ?, ?)");

            statement.setString(1, rpPlayer.getUuid());
            statement.setString(2, rpPlayer.getName());
            statement.setInt(3, rpPlayer.getAmountOfChars());
            statement.setInt(4, rpPlayer.getSelectedChar());


            statement.executeUpdate();
            Refactor.sendFormattedInfo("%s's RPplayer was saved", rpPlayer.getName());


            statement.close();

        }

    }

    public RPPlayer getRPPlayer(Player player) {

        try (Connection connection = DataSource.getConnection()) {

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM rpplayers WHERE uuid = ?");

            statement.setString(1, player.getUniqueId().toString());

            ResultSet rs = statement.executeQuery();

            RPPlayer rpPlayer = null;

            if(rs.next()) {
                rpPlayer = new RPPlayer(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getInt(4)
                );
            }

            return rpPlayer;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
   }

   public Character getCharacter(Player player, int charId) {

       try (Connection connection = DataSource.getConnection()) {

           PreparedStatement statement = connection.prepareStatement("SELECT * FROM characters WHERE owner_uuid = ? AND char_id = ?");

           statement.setString(1, player.getUniqueId().toString());
           statement.setInt(2, charId);

           ResultSet rs = statement.executeQuery();

           Character character;

           if(rs.next()) {
               character = new Character(
                       rs.getLong(1),
                       rs.getString(2),
                       rs.getString(3),
                       rs.getString(4),
                       rs.getString(5),
                       rs.getString(6),
                       rs.getString(7),
                       rs.getString(8),
                       rs.getInt(9)
               );

               return character;
           }

           return null;

       } catch (SQLException e) {
           e.printStackTrace();
           return null;
       }
   }

   public void updateCharacter(Character character) {
       try (Connection connection = DataSource.getConnection()) {

           PreparedStatement statement = connection.prepareStatement("UPDATE characters SET char_name = ?, char_role = ?, property_name = ?, property_value = ?, property_signature = ? WHERE id = ?");

           statement.setString(1, character.getCharName());
           statement.setString(2, character.getCharRole());
           statement.setString(3, character.getPropertyName());
           statement.setString(4, character.getPropertyValue());
           statement.setString(5, character.getPropertySignature());
           statement.setLong(6, character.getId());

           Refactor.sendFormattedInfo("%s's character #%d %s was updated", character.getOwnerName(), character.getCharId(), character.getCharName());
           statement.executeUpdate();

       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

   public int getLastCharId() {
       try(Connection con = DataSource.getConnection()) {
           PreparedStatement statement = con.prepareStatement("SELECT MAX(id) AS `max_id` FROM characters");
           ResultSet result = statement.executeQuery();
           if(result.next()){
               return result.getInt("max_id");
           }
           return 0;
       } catch (SQLException e) {
           Refactor.sendWarn("Database error");
           e.printStackTrace();
           return -1;
       }
   }

   public int getLastCharIdByPlayer(Player player) {
       try(Connection con = DataSource.getConnection()) {
           PreparedStatement statement = con.prepareStatement("SELECT MAX(char_id) as id FROM characters WHERE owner_uuid = ?");
           statement.setString(1, player.getUniqueId().toString());
           ResultSet result = statement.executeQuery();
           if(result.next()){
               return result.getInt("id");
           }
           return 0;
       } catch (SQLException e) {
           Refactor.sendWarn("Database error");
           e.printStackTrace();
           return -1;
       }
   }
}

package me.arahis.rpcharacter.database;

import me.arahis.rpcharacter.models.Character;
import me.arahis.rpcharacter.models.RPPlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHandler {

    public void initTables() {
        try (Connection connection = DataSource.getConnection()) {

            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS characters(id INT NOT NULL PRIMARY KEY, owner_name varchar(25), owner_uuid varchar(36), char_role varchar(32), char_name varchar(32), property_name varchar(255), property_value varchar(255), property_signature varchar(255))");
            PreparedStatement statement1 = connection.prepareStatement("CREATE TABLE IF NOT EXISTS rpplayers(uuid varchar(36) NOT NULL PRIMARY KEY, name varchar(16), amount_of_chars int, selected_char int)");

            try {
                statement.execute();
                System.out.println("Table characters was created");
            } catch (SQLException ex) {
                System.out.println("Error while table characters creation");
                ex.printStackTrace();
            }

            try {
                statement1.execute();
                System.out.println("Table rpplayer was created");
            } catch (SQLException ex) {
                System.out.println("Error while table rpplayers creation");
                ex.printStackTrace();
            }

            statement.close();
            statement1.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createChar(Character character) {

        try (Connection connection = DataSource.getConnection()) {

            PreparedStatement statement = connection.prepareStatement("INSERT INTO characters(owner_name, owner_uuid, char_role, char_name, property_name, property_value, property_signature) VALUES (?, ?, ?, ?, ?, ?, ?)");

            statement.setString(1, character.getOwnerName());
            statement.setString(2, character.getOwnerUUID());
            statement.setString(3, character.getCharRole());
            statement.setString(4, character.getCharName());
            statement.setString(5, character.getPropertyName());
            statement.setString(6, character.getPropertyValue());
            statement.setString(7, character.getPropertySignature());

            try {
                statement.executeUpdate();
                System.out.printf("%s's character %s was saved%n", character.getOwnerName(), character.getCharName());
            } catch (SQLException e) {
                System.out.printf("%s's character %s was failed to save%n", character.getOwnerName(), character.getCharName());
                e.printStackTrace();
            }

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public RPPlayer getRPPlayer(Player player) {

        try (Connection connection = DataSource.getConnection()) {

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM rpplayers WHERE uuid = ?");

            statement.setString(1, player.getUniqueId().toString());

            ResultSet rs = statement.executeQuery();

            RPPlayer rpPlayer = null;

            if(rs.next()) {
                rpPlayer = new RPPlayer(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4));
            }

            return rpPlayer;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

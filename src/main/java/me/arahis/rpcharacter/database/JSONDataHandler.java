package me.arahis.rpcharacter.database;

import com.google.gson.Gson;
import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.models.Character;
import me.arahis.rpcharacter.models.RPPlayer;
import me.arahis.rpcharacter.utils.Refactor;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JSONDataHandler implements IDataHandler {

    List<Character> characters = new ArrayList<>();
    List<RPPlayer> rpPlayers = new ArrayList<>();

    public void saveCharacters() throws IOException {

        Gson gson = new Gson();
        File file = new File(RPCharacterPlugin.getPlugin().getDataFolder().getAbsolutePath() + "/characters.json");
        file.getParentFile().mkdir();
        file.createNewFile();
        Writer writer = new FileWriter(file, false);
        gson.toJson(characters, writer);
        writer.flush();
        writer.close();
        Refactor.sendInfo("Characters saved!");

    }


    public void loadCharacters() throws IOException {

        Gson gson = new Gson();
        File file = new File(RPCharacterPlugin.getPlugin().getDataFolder().getAbsolutePath() + "/characters.json");
        if(file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Character[] c = gson.fromJson(reader, Character[].class);
                characters = new ArrayList<>(Arrays.asList(c));
                Refactor.sendInfo("Characters loaded!");
            }
        }
    }

    public void saveRPPlayers() throws IOException {

        Gson gson = new Gson();
        File file = new File(RPCharacterPlugin.getPlugin().getDataFolder().getAbsolutePath() + "/rpplayers.json");
        file.getParentFile().mkdir();
        file.createNewFile();
        Writer writer = new FileWriter(file, false);
        gson.toJson(rpPlayers, writer);
        writer.flush();
        writer.close();
        Refactor.sendInfo("RPPlayers saved!");

    }

    public void loadRPPlayers() throws IOException {

        Gson gson = new Gson();
        File file = new File(RPCharacterPlugin.getPlugin().getDataFolder().getAbsolutePath() + "/rpplayers.json");
        if(file.exists()) {
            try (Reader reader = new FileReader(file)) {
                RPPlayer[] r = gson.fromJson(reader, RPPlayer[].class);
                rpPlayers = new ArrayList<>(Arrays.asList(r));
                Refactor.sendInfo("RPPlayers loaded!");
            }
        }
    }

    @Override
    public List<Character> getAllCharacters() {
        return characters;
    }

    @Override
    public List<Character> getPlayerCharacters(Player player) {
        return characters.stream().filter(character -> character.getOwnerUUID().equals(player.getUniqueId().toString())).collect(Collectors.toList());
    }

    @Override
    public Character getCharacter(Player player, int id) {
        return characters.stream().filter(character -> character.getOwnerUUID().equals(player.getUniqueId().toString()) && character.getCharId() == id).findFirst().orElse(null);
    }

    @Override
    public void saveCharacter(Character character, SavingType type) {
        if(type.equals(SavingType.SAVE)) {
            characters.add(character);
        }
    }

    @Override
    public void deleteCharacter(Player player, int id) {
        characters.removeIf(character -> character.getOwnerUUID().equals(player.getUniqueId().toString()) && character.getCharId() == id);
        for(Character character : getPlayerCharacters(player)) {
            if(character.getCharId() > id) {
                character.setCharId(character.getCharId() - 1);
            }
        }
    }

    @Override
    public List<RPPlayer> getAllRPPlayers() {
        return rpPlayers;
    }

    @Override
    public RPPlayer getRPPlayer(Player player) {
        return rpPlayers.stream().filter(rpPlayer -> rpPlayer.getUuid().equals(player.getUniqueId().toString())).findFirst().orElse(null);
    }

    @Override
    public void saveRPPlayer(RPPlayer rpPlayer, SavingType type) {
        if(type.equals(SavingType.SAVE)) {
            rpPlayers.add(rpPlayer);
        }
    }

    @Override
    public void deleteRPPlayer(Player player) {
        characters.removeIf(character -> character.getOwnerUUID().equals(player.getUniqueId().toString()));
        rpPlayers.removeIf(rpPlayer -> rpPlayer.getUuid().equals(player.getUniqueId().toString()));
    }

    @Override
    public int getLastCharId() {
        int max = 1;
        for(Character character : characters) {
            if(character.getCharId() > max) {
                max = character.getCharId();
            }
        }
        return max;
    }

    @Override
    public int getLastCharIdByPlayer(Player player) {
        int max = 1;
        for(Character character : characters.stream().filter(character -> character.getOwnerUUID().equals(player.getUniqueId().toString())).collect(Collectors.toList())) {
            if(character.getCharId() > max) {
                max = character.getCharId();
            }
        }
        return max;
    }

}

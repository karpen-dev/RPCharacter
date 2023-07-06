package me.arahis.rpcharacter.utils;

import me.arahis.rpcharacter.models.Character;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CharCreationUtils {

    private HashMap<Player, Character> chars = new HashMap<>();
    public void addChar(Player p, Character c) {
        chars.put(p, c);
    }

    public void removeChar(Player p) {
        chars.remove(p);
    }

    public Character getChar(Player p) {
        return chars.get(p);
    }

    public HashMap<Player, Character> getChars() {
        return chars;
    }

    public void setChars(HashMap<Player, Character> chars) {
        this.chars = chars;
    }
}

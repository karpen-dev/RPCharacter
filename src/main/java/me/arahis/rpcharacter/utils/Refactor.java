package me.arahis.rpcharacter.utils;

import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.models.Character;
import me.clip.placeholderapi.PlaceholderAPI;
import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.property.IProperty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Refactor {

    public static String prefix = RPCharacterPlugin.getPlugin().getConfig().getString("prefix");
    public static String color(String toColor) {
        return ChatColor.translateAlternateColorCodes('&', toColor);
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(color(prefix + message));
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(color(prefix + message));
    }

    public static void sendMessageFromConfig(Player player, String path) {
        player.sendMessage(color(prefix + RPCharacterPlugin.getPlugin().getConfig().getString(path)));
    }

    public static void sendMessageFromConfig(CommandSender sender, String path) {
        sender.sendMessage(color(prefix + RPCharacterPlugin.getPlugin().getConfig().getString(path)));
    }

    public static void sendInfo(String message) {
        if(RPCharacterPlugin.getPlugin().getConfig().getBoolean("debug")) {
            Bukkit.getLogger().info(message);
        }
    }

    public static void sendFormattedInfo(String message, Object ... args) {
        if(RPCharacterPlugin.getPlugin().getConfig().getBoolean("debug")) {
            Bukkit.getLogger().info(String.format(message, args));
        }
    }

    public static void sendWarn(String message) {
        if(RPCharacterPlugin.getPlugin().getConfig().getBoolean("debug")) {
            Bukkit.getLogger().warning(message);
        }
    }

    public static void sendFormattedWarn(String message, Object ... args) {
        if(RPCharacterPlugin.getPlugin().getConfig().getBoolean("debug")) {
            Bukkit.getLogger().warning(String.format(message, args));
        }
    }

    public static String stringBuilder(String[] args, int startPos) {
        StringBuilder sb = new StringBuilder();
        for(int i = startPos; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim();
    }

    public static boolean isNotANumber(String toCheck) {
        try {
            Integer.parseInt(toCheck);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public static boolean isURL(String toCheck) {
        try {
            URL url = new URL(toCheck);
            return true;
        } catch (MalformedURLException ignored) {
            return false;
        }
    }

    public static void setDisplayName(Player player, Character character) {
        player.setDisplayName(PlaceholderAPI.setPlaceholders(player, color(RPCharacterPlugin.getPlugin().getConfig().getString("name-in-chat").replace("{role}", character.getCharRole()).replace("{name}", character.getCharName()).replace("{realname}", player.getName()))));
    }

    public static void setCharacterToPlayer(Player player, Character character) {
        IProperty property = RPCharacterPlugin.getPlugin().getSkinsRestorerAPI().createPlatformProperty(character.getPropertyName(), character.getPropertyValue(), character.getPropertySignature());
        RPCharacterPlugin.getPlugin().getSkinsRestorerAPI().applySkin(new PlayerWrapper(player), property);
        setDisplayName(player, character);
    }

    public static List<String> getPartialMatches(String arg, List<String> options) {
        return StringUtil.copyPartialMatches(arg, options.stream().filter(Objects::nonNull).collect(Collectors.toList()), new ArrayList<>());
    }


}

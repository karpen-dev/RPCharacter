package me.arahis.rpcharacter.utils;

import me.arahis.rpcharacter.RPCharacterPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Refactor {

    public static String prefix = RPCharacterPlugin.getPlugin().getConfig().getString("prefix");
    public static String color(String toColor) {
        return ChatColor.translateAlternateColorCodes('&', toColor);
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(color(prefix) + message);
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(color(prefix) + message);
    }

    public static void sendMessageFromConfig(Player player, String path) {
        player.sendMessage(color(prefix) + RPCharacterPlugin.getPlugin().getConfig().getString(path));
    }

    public static void sendMessageFromConfig(CommandSender sender, String path) {
        sender.sendMessage(color(prefix) + RPCharacterPlugin.getPlugin().getConfig().getString(path));
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

    public static boolean isInt(String toCheck) {
        try {
            Integer.parseInt(toCheck);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }


}

package me.arahis.rpcharacter.utils;

import me.arahis.rpcharacter.RPCharacterPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Refactor {

    public static String prefix = RPCharacterPlugin.getPlugin().getConfig().getString("prefix");
    public static String color(String toColor) {
        return ChatColor.translateAlternateColorCodes('&', toColor);
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(color(prefix) + " " + message);
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(color(prefix) + " " + message);
    }

    public static void sendMessageFromConfig(Player player, String path) {
        player.sendMessage(color(prefix) + " " + RPCharacterPlugin.getPlugin().getConfig().getString(path));
    }

    public static void sendMessageFromConfig(CommandSender sender, String path) {
        sender.sendMessage(color(prefix) + " " + RPCharacterPlugin.getPlugin().getConfig().getString(path));
    }
}

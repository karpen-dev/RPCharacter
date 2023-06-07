package me.arahis.rpcharacter.commands.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CharSetSkinTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {



        if(args.length == 1) {
            List<String> format = new ArrayList<>();
            format.add("nick");
            format.add("url");
            return format;
        }

        if(args.length == 4 && args[0].equals("url")) {
            List<String> skinFormat = new ArrayList<>();
            skinFormat.add("classic");
            skinFormat.add("slim");
            return skinFormat;
        }

        return null;
    }
}

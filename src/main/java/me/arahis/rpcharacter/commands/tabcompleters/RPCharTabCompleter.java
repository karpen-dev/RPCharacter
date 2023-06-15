package me.arahis.rpcharacter.commands.tabcompleters;

import me.arahis.rpcharacter.utils.Refactor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RPCharTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length == 1) {
            return Refactor.getPartialMatches(args[0], Collections.singletonList("reload"));
        }

        return new ArrayList<>();
    }
}

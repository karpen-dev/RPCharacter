package me.arahis.rpcharacter.commands.tabcompleters;

import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.database.IDataHandler;
import me.arahis.rpcharacter.utils.Refactor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CharSetSkinTabCompleter implements TabCompleter {

    private int amount;
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        RPCharacterPlugin plugin = RPCharacterPlugin.getPlugin();
        IDataHandler dataHandler = plugin.getDataHandler();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
           setAmount(dataHandler.getRPPlayer((Player) sender).getAmountOfChars());
        });

        List<String> ids = new ArrayList<>();

        for(int i = 1; i <= getAmount(); i++) {
            ids.add(String.valueOf(i));
        }

        if(args.length == 1) {
            List<String> format = new ArrayList<>();
            format.add("nick");
            format.add("url");
            return Refactor.getPartialMatches(args[0], format);
        }

        if(args.length == 2) {
            return Refactor.getPartialMatches(args[1], ids);
        }

        if(args.length == 4 && args[0].equals("url")) {
            List<String> skinFormat = new ArrayList<>();
            skinFormat.add("classic");
            skinFormat.add("slim");
            return Refactor.getPartialMatches(args[3], skinFormat);
        }

        return new ArrayList<>();
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }



}

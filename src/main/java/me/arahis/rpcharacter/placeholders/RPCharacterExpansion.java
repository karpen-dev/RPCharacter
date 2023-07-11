package me.arahis.rpcharacter.placeholders;

import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.database.IDataHandler;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RPCharacterExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "rpchar";
    }

    @Override
    public @NotNull String getAuthor() {
        return "arashi";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {

        RPCharacterPlugin plugin = RPCharacterPlugin.getPlugin();
        IDataHandler dataHandler = plugin.getDataHandler();

        String name = "";

        CompletableFuture<Integer> selectedCharId = CompletableFuture.supplyAsync(() -> dataHandler.getRPPlayer(player).getSelectedChar());
        int id = 0;
        try {
            id = selectedCharId.get();
        } catch (InterruptedException | ExecutionException e) {
            for(Player op : Bukkit.getOnlinePlayers()) {
                if(op.isOp()) {
                    op.sendMessage("Ошибка получения плейсхолдера, чекни консоль");
                }
            }
            e.printStackTrace();
        }
        if(params.equals("fullname")) {
            if(id == 1) {
                return "Нон-РП персонаж";
            } else if (id < 1) {
                return "Ошибка!";
            } else {
                return player.getDisplayName();
            }
        }
        return null;
    }
}

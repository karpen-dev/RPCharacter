package me.arahis.rpcharacter.menu;

import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.database.IDataHandler;
import me.arahis.rpcharacter.database.SavingType;
import me.arahis.rpcharacter.models.Character;
import me.arahis.rpcharacter.models.RPPlayer;
import me.arahis.rpcharacter.utils.Refactor;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.heads.SkullCreator;
import me.kodysimpson.simpapi.menu.Menu;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import me.nemo_64.spigotutilities.playerinputs.chatinput.PlayerChatInput;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.SkinVariant;
import net.skinsrestorer.api.exception.SkinRequestException;
import net.skinsrestorer.api.property.IProperty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Collections;

public class SkinTypeSelectionMenu extends Menu {
    public SkinTypeSelectionMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "Тип скина";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) throws MenuManagerNotSetupException, MenuManagerException {

        if(e.getCurrentItem() == null) return;

        SkinVariant variant = null;

        if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equals("Классический")) {
            variant = SkinVariant.CLASSIC;
        }

        if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equals("Слим")) {
            variant = SkinVariant.SLIM;
        }

        p.closeInventory();

        RPCharacterPlugin plugin = RPCharacterPlugin.getPlugin();
        IDataHandler handler = plugin.getDataHandler();

        int charid = (int) playerMenuUtility.getData("charid");

        if(playerMenuUtility.getData("skinsettype").equals("url")) {

            SkinVariant finalVariant = variant;

            PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(RPCharacterPlugin.getPlugin(), p);

            builder.isValidInput((p, str) -> {
                try {
                    plugin.getSkinsRestorerAPI().genSkinUrl(str, finalVariant);
                    return true;
                } catch (SkinRequestException ex) {
                    ex.printStackTrace();
                    return false;
                }
            });

            builder.setValue((p, str) -> str);

            builder.onInvalidInput((p, str) -> {
                Refactor.sendMessage(p, "Указана недействительная ссылка!");
                Refactor.sendMessage(p, "Попробуйте снова");
                return false;
            });

            builder.onCancel((p) -> Refactor.sendMessage(p, "Отмена установки скина!"));

            SkinVariant finalVariant1 = variant;
            builder.onFinish((p, value) -> {

                try {
                    IProperty property = plugin.getSkinsRestorerAPI().genSkinUrl(value, finalVariant1);

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

                        Character character = handler.getCharacter(p, charid);

                        character.setPropertyName(property.getName());
                        character.setPropertyValue(property.getValue());
                        character.setPropertySignature(property.getSignature());

                        try {
                            handler.saveCharacter(character, SavingType.UPDATE);
                        } catch (SQLException ex) {
                            Refactor.sendFormattedWarn("%s's character #%d %s wasn't successfully updated", character.getOwnerName(), character.getCharId(), character.getOwnerName());
                            Refactor.sendMessageFromConfig(p, "db-con-error");
                            ex.printStackTrace();
                            return;
                        }
                        Refactor.sendInfo("Skin updated by url: " + value);
                        Refactor.sendInfo("Skin variant: " + finalVariant1);

                        // Скин персонажа #%d %s был изменен по ссылке
                        // url
                        Refactor.sendMessage(p, String.format(plugin.getConfig().getString("skin-updated-by-url"), charid, character.getCharName()));
                        p.spigot().sendMessage(new ComponentBuilder(value)
                                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, value))
                                .event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder("Нажмите, чтобы открыть ссылку").create()))
                                .underlined(true)
                                .create()
                        );

                        RPPlayer rpPlayer = handler.getRPPlayer(p);
                        if(rpPlayer.getSelectedChar() == charid) {
                            plugin.getSkinsRestorerAPI().applySkin(new PlayerWrapper(p), property);
                        }

                    });

                } catch (SkinRequestException ex) {
                    Refactor.sendMessage(p, "Ошибка SkinsRestorerAPI! Попробуйте снова позже.");
                    Refactor.sendMessage(p, "Если ошибка осталась, создайте тикет в поддержке!");
                    ex.printStackTrace();
                }

            });

            builder.sendValueMessage(Refactor.color(Refactor.prefix + "Напишите в чат ссылку, по которой хотите изменить скин"));
            builder.toCancel("Отмена");

            PlayerChatInput<String> in = builder.build();

            in.start();
            Refactor.sendMessage(p, "&c\"Отмена\"&r, если хотите отменить действие");
            return;
        }

        if(playerMenuUtility.getData("skinsettype").equals("name")) {

            PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(RPCharacterPlugin.getPlugin(), p);

            builder.isValidInput((p, str) -> {
                IProperty property = plugin.getSkinsRestorerAPI().getSkinData(str);
                return property != null;
            });

            builder.onInvalidInput((p, str) -> {
                // Скин игрока %s не найден!
                Refactor.sendMessage(p, String.format(plugin.getConfig().getString("skin-not-found"), str));
                Refactor.sendMessage(p, "Попробуйте другой ник или напишите &c\"Отмена\"");
                return false;
            });

            builder.setValue((p, str) -> str);

            builder.onCancel((p) -> Refactor.sendMessage(p, "Отмена установки скина!"));

            builder.onFinish((p, value) -> Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

                Character character = handler.getCharacter(p, charid);

                IProperty property = plugin.getSkinsRestorerAPI().getSkinData(value);

                character.setPropertyName(property.getName());
                character.setPropertyValue(property.getValue());
                character.setPropertySignature(property.getSignature());

                try {
                    handler.saveCharacter(character, SavingType.UPDATE);
                } catch (SQLException ex) {
                    Refactor.sendFormattedWarn("%s's character #%d %s wasn't successfully updated", character.getOwnerName(), character.getCharId(), character.getOwnerName());
                    Refactor.sendMessageFromConfig(p, "db-con-error");
                    ex.printStackTrace();
                    return;
                }
                Refactor.sendFormattedInfo("Skin updated by nickname to %s's skin", value);

                // Скин персонажа #%d %s был изменен по нику %s
                Refactor.sendMessage(p, String.format(plugin.getConfig().getString("skin-updated-by-nick"), charid, character.getCharName(), value));

                RPPlayer rpPlayer = handler.getRPPlayer(p);
                if (rpPlayer.getSelectedChar() == charid) {
                    plugin.getSkinsRestorerAPI().applySkin(new PlayerWrapper(p), property);
                }

            }));

            builder.sendValueMessage(Refactor.color(Refactor.prefix + "Напишите в чат ник, по которому хотите изменить скин"));
            builder.toCancel("Отмена");

            PlayerChatInput<String> in = builder.build();

            in.start();
            Refactor.sendMessage(p, "&c\"Отмена\"&r, если хотите отменить действие");
        }
    }

    @Override
    public void setMenuItems() {

        ItemStack classic = SkullCreator.itemFromBase64("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA3NzhmNDdjOTkxNDFlODk5ZjhlYTUyZGEzNDFhNDkyYmYzMzFhZGVmZThmZGNjMmRjZjUwODI0ZTgxNDQxZiJ9fX0=");
        ItemMeta classicMeta = classic.getItemMeta();
        classicMeta.setDisplayName(Refactor.color("&fКлассический"));
        classicMeta.setLore(Collections.singletonList(Refactor.color("&eНажмите, чтобы выбрать классический(широкий) тип скина")));
        classic.setItemMeta(classicMeta);

        ItemStack slim = SkullCreator.itemFromBase64("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDZhY2QwNmU4NDgzYjE3NmU4ZWEzOWZjMTJmZTEwNWViM2EyYTQ5NzBmNTEwMDA1N2U5ZDg0ZDRiNjBiZGZhNyJ9fX0=");
        ItemMeta slimMeta = slim.getItemMeta();
        slimMeta.setDisplayName(Refactor.color("&fСлим"));
        slimMeta.setLore(Collections.singletonList(Refactor.color("&eНажмите, чтобы выбрать слим(зауженный) тип скина")));
        slim.setItemMeta(slimMeta);

        inventory.setItem(3, classic);
        inventory.setItem(5, slim);
    }
}

package me.arahis.rpcharacter.menu;

import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.database.SavingType;
import me.arahis.rpcharacter.models.Character;
import me.arahis.rpcharacter.models.RPPlayer;
import me.arahis.rpcharacter.utils.CharCreationUtils;
import me.arahis.rpcharacter.utils.Refactor;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.Menu;
import me.kodysimpson.simpapi.menu.MenuManager;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import me.nemo_64.spigotutilities.playerinputs.chatinput.PlayerChatInput;
import net.skinsrestorer.api.SkinVariant;
import net.skinsrestorer.api.exception.SkinRequestException;
import net.skinsrestorer.api.property.IProperty;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

public class CharacterCreationMenu extends Menu {
    public CharacterCreationMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "Создание";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    RPCharacterPlugin plugin = RPCharacterPlugin.getPlugin();
    CharCreationUtils charCreationUtils = plugin.getCharCreationUtils();

    public SkinVariant getSkinVariant() {
        return skinVariant;
    }

    public void setSkinVariant(SkinVariant skinVariant) {
        this.skinVariant = skinVariant;
    }

    SkinVariant skinVariant;

    @Override
    public void handleMenu(InventoryClickEvent e) throws MenuManagerNotSetupException, MenuManagerException {

        if(e.getCurrentItem() == null) return;

        ItemStack variantClassic = makeItem(Material.OAK_FENCE,
                Refactor.color("&fКлассический"),
                Refactor.color("&eНамжите, чтобы установить слим"));
        ItemStack variantSlim = makeItem(Material.END_ROD,
                Refactor.color("&fСлим"),
                Refactor.color("&eНажмите, чтобы установить классический"));

        switch (e.getCurrentItem().getType()) {
            case OAK_FENCE:
                setSkinVariant(SkinVariant.SLIM);
                inventory.setItem(12, variantSlim);
                break;
            case END_ROD:
                setSkinVariant(SkinVariant.CLASSIC);
                inventory.setItem(12, variantClassic);
                break;
            case NAME_TAG:
                new AnvilGUI.Builder()
                .onClose((stateSnapshot) -> {
                    try {
                        MenuManager.openMenu(CharacterCreationMenu.class, stateSnapshot.getPlayer());
                    } catch (MenuManagerException | MenuManagerNotSetupException ex) {
                        ex.printStackTrace();
                    }
                })
                .onClick((slot, stateSnapshot) -> {

                    if(slot != AnvilGUI.Slot.OUTPUT) return Collections.emptyList();

                    return Arrays.asList(
                            AnvilGUI.ResponseAction.close(),
                            AnvilGUI.ResponseAction.run(() -> {
                                charCreationUtils.getChar(p).setCharName(ChatColor.stripColor(stateSnapshot.getText()));
                                try {
                                    MenuManager.openMenu(CharacterCreationMenu.class, stateSnapshot.getPlayer());
                                } catch (MenuManagerException | MenuManagerNotSetupException ex) {
                                    ex.printStackTrace();
                                }
                            })
                    );
                })
                .preventClose()
                .text("Впишите сюда имя")
                .itemLeft(new ItemStack(Material.NAME_TAG))
                .title("Установка имени")
                .plugin(RPCharacterPlugin.getPlugin())
                .open(p);
                break;
            case BOOK:
                new AnvilGUI.Builder()
                        .onClose((stateSnapshot -> {
                            try {
                                MenuManager.openMenu(CharacterCreationMenu.class, stateSnapshot.getPlayer());
                            } catch (MenuManagerException | MenuManagerNotSetupException ex) {
                                ex.printStackTrace();
                            }
                        }))
                        .onClick((slot, stateSnapshot) -> {
                            if(slot != AnvilGUI.Slot.OUTPUT) return Collections.emptyList();

                            return Arrays.asList(
                                    AnvilGUI.ResponseAction.close(),
                                    AnvilGUI.ResponseAction.run(() -> {
                                        charCreationUtils.getChar(p).setCharRole(ChatColor.stripColor(stateSnapshot.getText()));
                                        try {
                                            MenuManager.openMenu(CharacterCreationMenu.class, stateSnapshot.getPlayer());
                                        } catch (MenuManagerException | MenuManagerNotSetupException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    })
                            );
                        })
                        .preventClose()
                        .title("Установки роли")
                        .text("Впишите сюда роль")
                        .itemLeft(new ItemStack(Material.BOOK))
                        .plugin(RPCharacterPlugin.getPlugin())
                        .open(p);
                System.out.println(charCreationUtils.getChar(p).getCharRole());
                break;
            case PLAYER_HEAD:
                PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(plugin, p);

                builder.isValidInput((p, str) -> {
                    try {
                        if(Refactor.isURL(str)) {
                            return plugin.getSkinsRestorerAPI().genSkinUrl(str, getSkinVariant()) != null;
                        } else {
                            return plugin.getSkinsRestorerAPI().getSkinData(str) != null;
                        }
                    } catch (SkinRequestException ex) {
                        ex.printStackTrace();
                        return false;
                    }
                });

                builder.setValue((p, str) -> str);

                builder.onCancel(p -> {
                    try {
                        MenuManager.openMenu(CharacterCreationMenu.class, p);
                    } catch (MenuManagerException | MenuManagerNotSetupException ex) {
                        ex.printStackTrace();
                    }
                });

                builder.expiresAfter(20*30);
                builder.onExpire(p -> {
                    try {
                        MenuManager.openMenu(CharacterCreationMenu.class, p);
                    } catch (MenuManagerException | MenuManagerNotSetupException ex) {
                        ex.printStackTrace();
                    }
                });

                builder.sendValueMessage(Refactor.color(Refactor.prefix + "Введите ник или ссылку!"));
                builder.toCancel("Отмена");

                builder.onInvalidInput((p, str) -> {
                    Refactor.sendMessage(p, "Скин не найден! В случае, если вы уверены в");
                    Refactor.sendMessage(p, "правильности введеных данных, то создайте тикет!");
                    return false;
                });

                builder.onFinish((p, str) -> {

                    IProperty property;
                    if(Refactor.isURL(str)) {
                        try {
                            property = plugin.getSkinsRestorerAPI().genSkinUrl(str, getSkinVariant());
                        } catch (SkinRequestException ex) {
                            Refactor.sendMessageFromConfig(p, "skinapi-exception");
                            ex.printStackTrace();
                            return;
                        }
                    } else {
                        property = plugin.getSkinsRestorerAPI().getSkinData(str);
                    }

                    charCreationUtils.getChar(p).setPropertyName(property.getName());
                    charCreationUtils.getChar(p).setPropertyValue(property.getValue());
                    charCreationUtils.getChar(p).setPropertySignature(property.getSignature());

                    try {
                        MenuManager.openMenu(CharacterCreationMenu.class, p);
                    } catch (MenuManagerException | MenuManagerNotSetupException ex) {
                        ex.printStackTrace();
                        Refactor.sendMessageFromConfig(p, "menu-exception");
                        return;
                    }

                });

                builder.build().start();
                p.closeInventory();
                Refactor.sendMessage(p, "&c\"Отмена\"&r, если хотите отменить действие");

                break;
            case ARROW:
                Character character = charCreationUtils.getChar(p);
                if(character.getCharRole() == null || character.getCharRole().length() <= 1) {
                    Refactor.sendMessage(p, "Установите роль");
                    return;
                }
                if(character.getCharName() == null || character.getCharName().length() <= 1) {
                    Refactor.sendMessage(p, "Установите ник");
                    return;
                }
                if(character.getPropertyName() == null || character.getPropertyValue() == null || character.getPropertySignature() == null) {
                    Refactor.sendMessage(p, "Установите скин");
                    return;
                }
                p.closeInventory();
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    if(plugin.getDataHandler().getRPPlayer(p).getAmountOfChars() >= plugin.getConfig().getInt("limit")) {
                        Refactor.sendMessageFromConfig(p, "limit-of-characters");
                        return;
                    }
                    try {
                        RPPlayer rpPlayer = plugin.getDataHandler().getRPPlayer(p);
                        rpPlayer.setAmountOfChars(rpPlayer.getAmountOfChars() + 1);
                        plugin.getDataHandler().saveCharacter(character, SavingType.SAVE);
                        plugin.getDataHandler().saveRPPlayer(rpPlayer, SavingType.UPDATE);
                        Refactor.sendMessage(p, String.format(plugin.getConfig().getString("char-created"), character.getCharId(), character.getCharRole(), character.getCharName()));
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
        }
    }

    @Override
    public void setMenuItems() {

        ItemStack name = makeItem(Material.NAME_TAG,
                Refactor.color("&fУстановить имя")
        );

        ItemStack role = makeItem(Material.BOOK,
                Refactor.color("&fУстановить роль")
        );

        ItemStack skin = makeItem(Material.PLAYER_HEAD,
                Refactor.color("&fУстановить скин"),
                Refactor.color("&eОбратите внимание на следующий предмет"),
                Refactor.color("&eУбедитесь, что выбрали нужный вариант скина")
        );


        ItemStack variantClassic = makeItem(Material.OAK_FENCE,
                Refactor.color("&fКлассический"),
                Refactor.color("&eНамжите, чтобы установить слим"));
        ItemStack variantSlim = makeItem(Material.END_ROD,
                Refactor.color("&fСлим"),
                Refactor.color("&eНажмите, чтобы установить классический"));

        ItemStack confirm = makeItem(Material.ARROW,
                Refactor.color("&aПодтвердить")
        );

        inventory.setItem(9, name);
        inventory.setItem(10, role);
        inventory.setItem(11, skin);
        inventory.setItem(12, variantClassic);
        inventory.setItem(17, confirm);

    }
}

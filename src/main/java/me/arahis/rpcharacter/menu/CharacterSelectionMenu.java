package me.arahis.rpcharacter.menu;

import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.database.IDataHandler;
import me.arahis.rpcharacter.database.SavingType;
import me.arahis.rpcharacter.models.Character;
import me.arahis.rpcharacter.models.RPPlayer;
import me.arahis.rpcharacter.utils.CharCreationUtils;
import me.arahis.rpcharacter.utils.Refactor;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.heads.SkullCreator;
import me.kodysimpson.simpapi.menu.Menu;
import me.kodysimpson.simpapi.menu.MenuManager;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.property.IProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CharacterSelectionMenu extends Menu {
    public CharacterSelectionMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    RPCharacterPlugin plugin = RPCharacterPlugin.getPlugin();
    IDataHandler handler = plugin.getDataHandler();

    @Override
    public String getMenuName() {
        return "Персонажи";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    RPCharacterPlugin pl = RPCharacterPlugin.getPlugin();
    CharCreationUtils charCreationUtils = pl.getCharCreationUtils();

    @Override
    public void handleMenu(InventoryClickEvent e) throws MenuManagerNotSetupException, MenuManagerException {

        if(e.getCurrentItem() == null) return;

        ItemStack item = e.getCurrentItem();

        NamespacedKey key = new NamespacedKey(plugin, "charid");

        if(e.isLeftClick()) {

            if (item.getType().equals(Material.WRITABLE_BOOK)) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    charCreationUtils.addChar(p, new Character((long) handler.getLastCharId() + 1,
                            p.getName(),
                            p.getUniqueId().toString(),
                            null,
                            null,
                            null,
                            null,
                            null,
                            handler.getLastCharIdByPlayer(p) + 1)
                    );
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        try {
                            MenuManager.openMenu(CharacterCreationMenu.class, p);
                        } catch (MenuManagerException | MenuManagerNotSetupException ex) {
                            Refactor.sendMessage(p, "menu-exception");
                            ex.printStackTrace();
                        }
                    });
                });

            } else if (item.getType().equals(Material.PLAYER_HEAD)) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    RPPlayer rpPlayer = handler.getRPPlayer(p);

                    if(rpPlayer.getSelectedChar() == item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER)) return;

                    rpPlayer.setSelectedChar(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER));

                    try {
                        handler.saveRPPlayer(rpPlayer, SavingType.UPDATE);
                    } catch (SQLException ex) {
                        Refactor.sendFormattedWarn("RPPlayer %s wasn't saved successfully", rpPlayer.getName());
                        Refactor.sendMessageFromConfig(p, "db-con-error");
                        ex.printStackTrace();
                        return;
                    }

                    Character character = handler.getCharacter(p, item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER));

                    IProperty property = plugin.getSkinsRestorerAPI().createPlatformProperty(character.getPropertyName(), character.getPropertyValue(), character.getPropertySignature());

                    plugin.getSkinsRestorerAPI().applySkin(new PlayerWrapper(p), property);

                    Refactor.setDisplayName(p, character);
                    // Персонаж #%d [%s] %s был выбран
                    Refactor.sendMessage(p, String.format(plugin.getConfig().getString("char-selected"), character.getCharId(), character.getCharRole(), character.getCharName()));
                    for(Player target : Bukkit.getOnlinePlayers()) {
                        if(target.equals(p)) continue;
                        if(target.getWorld().equals(p.getWorld())) {
                            if(target.getLocation().distanceSquared(p.getLocation()) <= Math.pow(128, 2)) {
                                Refactor.sendMessage(target, String.format("%s выбрал персонажа #%d [%s] %s", p.getName(), character.getCharId(), character.getCharRole(), character.getCharName()));
                            }
                        }
                    }

                });
                p.closeInventory();
            }

        } else if(e.isRightClick() && e.isShiftClick()) {

            int id = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
            playerMenuUtility.setData("charid", id);
            MenuManager.openMenu(EditOptionSelectionMenu.class, p);

        }

    }

    @Override
    public void setMenuItems() {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            RPPlayer rpPlayer = handler.getRPPlayer(p);
            Bukkit.getScheduler().runTask(plugin, () -> {

                inventory.setItem(22, makeItem
                        (
                                Material.WRITABLE_BOOK,
                                Refactor.color("&fСоздать персонажа"),
                                Refactor.color("&eНажмите, чтобы перейти в меню создания персонажа")
                        )
                );

                for(Character character : handler.getPlayerCharacters(p)) {

                    List<String> lore = new ArrayList<>();

                    if(character.getCharId() == rpPlayer.getSelectedChar()) {
                        lore.add("");
                        lore.add(Refactor.color("&a[ВЫБРАН]"));
                        lore.add(Refactor.color("&eShift + ПКМ → Редактировать персонажа"));
                    } else {
                        lore.add("");
                        lore.add(Refactor.color("&eЛКМ → Выбрать персонажа"));
                        lore.add(Refactor.color("&eShift + ПКМ → Редактировать персонажа"));
                    }

                    ItemStack charItem = SkullCreator.itemFromBase64(character.getPropertyValue());
                    ItemMeta charMeta = charItem.getItemMeta();
                    charMeta.setDisplayName(Refactor.color("&f" + String.format("#%d [%s] %s", character.getCharId(), character.getCharRole(), character.getCharName())));
                    charMeta.setLore(lore);
                    NamespacedKey key = new NamespacedKey(plugin, "charid");
                    charMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, character.getCharId());
                    charItem.setItemMeta(charMeta);

                    inventory.addItem(charItem);

                }
            });
        });

    }

}

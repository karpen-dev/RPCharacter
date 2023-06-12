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
import me.kodysimpson.simpapi.menu.MenuManager;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.property.IProperty;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
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
    List<String> lore = new ArrayList<>();

    @Override
    public String getMenuName() {
        return "Персонажи";
    }

    @Override
    public int getSlots() {
        return 18;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) throws MenuManagerNotSetupException, MenuManagerException {

        if(e.getCurrentItem() == null) return;

        ItemStack item = e.getCurrentItem();

        NamespacedKey key = new NamespacedKey(plugin, "charid");

        if(e.isLeftClick()) {

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                RPPlayer rpPlayer = handler.getRPPlayer(p);
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

            });

            return;
        }

        if(e.isRightClick() && e.isShiftClick()) {

            if(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER) == 1) {
                Refactor.sendMessage(p, "Нельзя удалить Нон-РП персонажа!");
                return;
            }

            playerMenuUtility.setData("charid", item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER));
            MenuManager.openMenu(ConfirmCharDeletionMenu.class, p);

        }

    }

    @Override
    public void setMenuItems() {

        lore.add("");
        lore.add(Refactor.color("&e&lЛКМ &r&e→ Выбрать персонажа"));
        lore.add(Refactor.color("&e&lShift + ПКМ &r&e→ Удалить персонажа"));

        for(Character character : handler.getPlayerCharacters(p)) {

            ItemStack charItem = SkullCreator.itemFromBase64(character.getPropertyValue());
            ItemMeta charMeta = charItem.getItemMeta();
            charMeta.setDisplayName(Refactor.color("&f" + String.format("#%d [%s] %s", character.getCharId(), character.getCharRole(), character.getCharName())));
            charMeta.setLore(lore);
            NamespacedKey key = new NamespacedKey(plugin, "charid");
            charMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, character.getCharId());
            charItem.setItemMeta(charMeta);

            inventory.addItem(charItem);

        }

    }

}

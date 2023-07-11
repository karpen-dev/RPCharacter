package me.arahis.rpcharacter.menu;

import me.arahis.rpcharacter.RPCharacterPlugin;
import me.arahis.rpcharacter.database.IDataHandler;
import me.arahis.rpcharacter.database.SavingType;
import me.arahis.rpcharacter.models.Character;
import me.arahis.rpcharacter.models.RPPlayer;
import me.arahis.rpcharacter.utils.Refactor;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.Menu;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class ConfirmCharDeletionMenu extends Menu {

    public ConfirmCharDeletionMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "Подтверждение";
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

        RPCharacterPlugin plugin = RPCharacterPlugin.getPlugin();
        IDataHandler handler = plugin.getDataHandler();

        if(e.getCurrentItem() == null) return;

        if(e.getCurrentItem().getType().equals(Material.GREEN_CONCRETE)) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

                int id = playerMenuUtility.getData("charid", Integer.class);

                if(id == 1) {
                    Refactor.sendMessage(p, "Нельзя удалять Нон-РП персонажа");
                    return;
                }

                Character character = handler.getCharacter(p, id);
                String charName = character.getCharName();
                String charRole = character.getCharRole();
                RPPlayer rpPlayer = handler.getRPPlayer(p);

                if(rpPlayer.getSelectedChar() == id) {
                    handler.replaceCharacter(plugin, p, rpPlayer, handler);
                }
                rpPlayer.setAmountOfChars(rpPlayer.getAmountOfChars() - 1);
                try {
                    handler.saveRPPlayer(rpPlayer, SavingType.UPDATE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                handler.deleteCharacter(p, playerMenuUtility.getData("charid", Integer.class));
                // Персонаж #%d [%s] %s был удален
                Refactor.sendMessage(p, String.format(plugin.getConfig().getString("char-deleted"), id, charRole, charName));

            });
            p.closeInventory();
            return;
        }

        if(e.getCurrentItem().getType().equals(Material.RED_CONCRETE)) {
            p.closeInventory();
        }

    }



    @Override
    public void setMenuItems() {

        ItemStack yes = makeItem(Material.GREEN_CONCRETE,
                Refactor.color("&aПодтвердить"),
                Refactor.color("&fНажмите, чтобы подтвердить действие"));
        ItemStack no = makeItem(Material.RED_CONCRETE,
                Refactor.color("&cОтменить"),
                Refactor.color("&fНажмите, чтобы отменить действие"));

        inventory.setItem(3, yes);
        inventory.setItem(5, no);

    }
}

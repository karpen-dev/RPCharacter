package me.arahis.rpcharacter.menu;

import me.arahis.rpcharacter.utils.Refactor;
import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.Menu;
import me.kodysimpson.simpapi.menu.MenuManager;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SkinSetTypeSelectionMenu extends Menu {
    public SkinSetTypeSelectionMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "Опции";
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

        if(e.getCurrentItem().getType().equals(Material.ARROW)) {
            playerMenuUtility.setData("skinsettype", "url");
        }

        if(e.getCurrentItem().getType().equals(Material.NAME_TAG)) {
            playerMenuUtility.setData("skinsettype", "name");
        }

        MenuManager.openMenu(SkinTypeSelectionMenu.class, p);

    }

    @Override
    public void setMenuItems() {

        ItemStack url = makeItem(
                Material.ARROW,
                Refactor.color("&fСсылка"),
                Refactor.color("&eНажмите, чтобы изменить скин ссылкой")
                );

        ItemStack name = makeItem(
                Material.NAME_TAG,
                Refactor.color("&fНик"),
                Refactor.color("&eНажмите, чтобы изменить скин ником")
        );

        inventory.setItem(3, url);
        inventory.setItem(5, name);

    }
}

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
import me.nemo_64.spigotutilities.playerinputs.chatinput.PlayerChatInput;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditOptionSelectionMenu extends Menu {

    public EditOptionSelectionMenu(PlayerMenuUtility playerMenuUtility) {
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

        if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equals("Скин")) {
            MenuManager.openMenu(SkinSetTypeSelectionMenu.class, p);
        }

        RPCharacterPlugin plugin = RPCharacterPlugin.getPlugin();
        IDataHandler handler = plugin.getDataHandler();

        if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equals("Роль")) {

            PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(plugin, p);
            builder.isValidInput((p, str) -> str.length() < 32);

            builder.setValue((p, str) -> str);

            builder.onInvalidInput((p, str) -> {
                Refactor.sendMessage(p, "Длина роли должна быть меньше 32");
                Refactor.sendMessage(p, "Попробуйте снова");
                return false;
            });

            int id = (int) playerMenuUtility.getData("charid");

            builder.onFinish((p, value) -> {

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

                    Character character = handler.getCharacter(p, id);
                    RPPlayer rpPlayer = handler.getRPPlayer(p);
                    if(character == null) {
                        // Персонаж с номером %d не найден!
                        Refactor.sendMessage(p, String.format(plugin.getConfig().getString("char-not-found"), id));
                        return;
                    }

                    character.setCharRole(value);

                    try {
                        handler.saveCharacter(character, SavingType.UPDATE);
                    } catch (SQLException ex) {
                        Refactor.sendFormattedWarn("%s's character #%d %s wasn't successfully updated", character.getOwnerName(), character.getCharId(), character.getOwnerName());
                        Refactor.sendMessageFromConfig(p, "db-con-error");
                        ex.printStackTrace();
                        return;
                    }

                    Refactor.sendFormattedInfo("Role updated to %s", value);

                    if(rpPlayer.getSelectedChar() == id) {
                        Refactor.setDisplayName(p, character);
                    }

                    // Роль персонажа #%d %s была изменена на %s
                    Refactor.sendMessage(p, String.format(plugin.getConfig().getString("role-updated"), id, character.getCharName(), character.getCharRole()));
                });

            });

            builder.onCancel((p) -> Refactor.sendMessage(p, "Отмена изменения роли!"));

            builder.sendValueMessage(Refactor.color(Refactor.prefix + "Напишите в чат роль, по которой хотите изменить роль"));
            builder.toCancel("Отмена");

            PlayerChatInput<String> in = builder.build();

            p.closeInventory();
            in.start();
            Refactor.sendMessage(p, "&c\"Отмена\"&r, если хотите отменить действие");

            return;
        }

        if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equals("Удалить")) {
            MenuManager.openMenu(ConfirmCharDeletionMenu.class, p);
        }

    }

    @Override
    public void setMenuItems() {


        RPCharacterPlugin plugin = RPCharacterPlugin.getPlugin();
        IDataHandler handler = plugin.getDataHandler();

        int charid = (int) playerMenuUtility.getData("charid");
        String skinValue = "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA3NzhmNDdjOTkxNDFlODk5ZjhlYTUyZGEzNDFhNDkyYmYzMzFhZGVmZThmZGNjMmRjZjUwODI0ZTgxNDQxZiJ9fX0=";

        ItemStack role = makeItem(
                Material.LECTERN,
                Refactor.color("&fРоль"),
                Refactor.color("&eНажмите, чтобы изменить роль")
        );

        ItemStack skin = SkullCreator.itemFromBase64(skinValue);
        ItemMeta skinMeta = skin.getItemMeta();
        skinMeta.setDisplayName(Refactor.color("&fСкин"));
        List<String> skinLore = new ArrayList<>();
        skinLore.add(Refactor.color("&eНажмите, чтобы изменить скин"));
        skinMeta.setLore(skinLore);
        skin.setItemMeta(skinMeta);

        ItemStack delete = makeItem(
                Material.BARRIER,
                Refactor.color("&cУдалить")
        );


        inventory.setItem(0, role);
        inventory.setItem(2, skin);

        inventory.setItem(8, delete);
    }
}

package top.mrxiaom.pixelmonstrengthen.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import top.mrxiaom.pixelmonstrengthen.PixelmonStrengthen;

public interface IGui {
    Player getPlayer();

    Inventory create();

    void onClick(InventoryClickEvent event);

    void onClose(InventoryCloseEvent event);

    default void refresh() {
        PixelmonStrengthen.getInstance().getGuiManager().openGui(this);
    }
}

package top.mrxiaom.pixelmonstrengthen;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

public interface GuiModel {
	Inventory create(Player player, Pokemon pokemon, int intValue);
	
	void onInventoryClose(InventoryCloseEvent event);
	
	void onInventoryClick(InventoryClickEvent event);
}

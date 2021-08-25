package top.mrxiaom.pixelmonstrengthen;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;

public class GuiFenJie implements GuiModel{
	Main main;
	public GuiFenJie(Main main) {
		this.main = main;
	}
	
	@Override
	public Inventory create(Player player, Pokemon p, int intValue) {
		Inventory inv = Bukkit.createInventory(null, 27, Utils.fenjie_title);
		ItemStack biankuang = Utils.getGlass(7, main.getMessage("frame-title"));
		inv.setItem(0, biankuang);
		inv.setItem(1, biankuang);
		inv.setItem(2, biankuang);
		inv.setItem(3, biankuang);
		inv.setItem(4, biankuang);
		inv.setItem(5, biankuang);
		inv.setItem(6, biankuang);
		inv.setItem(7, biankuang);
		inv.setItem(8, biankuang);

		inv.setItem(9, biankuang);
		inv.setItem(17, biankuang);

		inv.setItem(18, biankuang);
		inv.setItem(19, biankuang);
		inv.setItem(20, biankuang);
		inv.setItem(21, biankuang);
		inv.setItem(22, biankuang);
		inv.setItem(23, biankuang);
		inv.setItem(24, biankuang);
		inv.setItem(25, biankuang);
		inv.setItem(26, biankuang);
		PlayerPartyStorage pStorage = Pixelmon.storageManager.getParty(player.getUniqueId());
		for (int i = 0; i < 6; i++) {
			Pokemon pokemon = pStorage.get(i);
			
			ItemStack item = (pokemon == null ? new ItemStack(Material.BARRIER) : Utils.getPixelmonItem(pokemon));
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(
					pokemon == null ? main.getMessage("no-pokemon-item") : ("§e§l" +(pokemon.getNickname() == null ? pokemon.getDisplayName()
							: pokemon.getNickname())+" §e§l("+ Utils.getPixelmonI18nName(pokemon) +")"));
			
			if (pokemon != null) {

				int v = Utils.getV(pokemon);
				List<String> lores = new ArrayList<String>();
				int amount = main.getConfig().getInt("amount");
				int per = main.getSoulIvs(pokemon.getBaseStats().getPokemonName());
				for (String s : main.getConfig().getStringList("decompose-lore")) {
					lores.add(s.replace("&", "§").replace("%amount%", "" + amount).replace("%per%", "" + per));
				}
				if (main.getSoulIvs(pokemon.getBaseStats().getPokemonName()) <= 0) {
					lores.add(main.getMessage("can-not-fenjie-gui"));
				}
				item.setAmount( v == 0 ? 1 : v);
				im.setLore(lores);
			}
			item.setItemMeta(im);
			inv.setItem(10 + i, item);
		}
		return inv;
	}

	@Override
	public void onInventoryClose(InventoryCloseEvent event) {
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {

		Player p = (Player) event.getWhoClicked();
		String prefix = main.getConfig().getString("prefix").replace("&", "§");
		if (event.getInventory().getTitle().equalsIgnoreCase(Utils.fenjie_title)) {
			event.setCancelled(true);
			p.updateInventory();
			if (event.getRawSlot() >= 10 && event.getRawSlot() <= 15) {
				int slot = event.getRawSlot() - 10;
				PlayerPartyStorage pStorage = Pixelmon.storageManager.getParty(p.getUniqueId());
				if (pStorage == null) {
					p.closeInventory();
					p.sendMessage(prefix + main.getMessage("no-team"));
					return;
				}
				if (slot < 0 || slot > 6) {
					p.closeInventory();
					p.sendMessage(prefix + main.getMessage("unknown-slot"));
					return;
				}
				Pokemon pokemon = pStorage.get(slot);
				if (pokemon == null) {
					return;
				}
				if (pStorage.countAll() <= 1) {
					p.sendMessage(prefix + main.getMessage("last-pokemon"));
					return;
				}
				if (main.getSoulIvs(pokemon.getBaseStats().getPokemonName()) <= 0) {
					p.sendMessage(prefix + main.getMessage("can-not-fenjie"));
					return;
				}
				p.closeInventory();
				p.openInventory(main.getGuiFenJieConfirm().create(null, pokemon, slot));
			}
		}
	}
	
}

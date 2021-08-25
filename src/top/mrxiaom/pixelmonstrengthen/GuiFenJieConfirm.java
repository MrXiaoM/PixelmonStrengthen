package top.mrxiaom.pixelmonstrengthen;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;

public class GuiFenJieConfirm implements GuiModel{
	Main main;
	public GuiFenJieConfirm(Main main) {
		this.main = main;
	}

	@Override
	public Inventory create(Player player, Pokemon pokemon, int intValue) {
		Inventory inv = Bukkit.createInventory(null, 27, Utils.fenjie_confirm_title);
		ItemStack biankuang = Utils.getGlass(14, main.getMessage("frame-title"));
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

		int v = Utils.getV(pokemon);
		String pokemon_nickname = pokemon.getNickname() == null ? pokemon.getDisplayName()
				: pokemon.getNickname();
		String pokemon_displayname = pokemon.getDisplayName();
		int amount = main.getConfig().getInt("amount");
		int per = main.getSoulIvs(pokemon.getBaseStats().getPokemonName());
		List<String> lore = new ArrayList<String>();
		
		lore.add("" + intValue);
		lore.add("§7===[§b个体值§7]===");
		lore.add("  §a§l" + main.getMessage("soul-type-1") + ": " + pokemon.getIVs().getStat(StatsType.HP));
		lore.add("  §a§l" + main.getMessage("soul-type-2") + ": " + pokemon.getIVs().getStat(StatsType.Attack));
		lore.add("  §a§l" + main.getMessage("soul-type-3") + ": " + pokemon.getIVs().getStat(StatsType.Defence));
		lore.add("  §a§l" + main.getMessage("soul-type-4") + ": " + pokemon.getIVs().getStat(StatsType.SpecialAttack));
		lore.add("  §a§l" + main.getMessage("soul-type-5") + ": " + pokemon.getIVs().getStat(StatsType.SpecialDefence));
		lore.add("  §a§l" + main.getMessage("soul-type-6") + ": " + pokemon.getIVs().getStat(StatsType.Speed));
		lore.add("§e分解将获得 §a" + pokemon_displayname + " §e灵魂 *" +amount);
		lore.add("§e每个碎片可增加 §a" + per+" §e点个体值");
		
		ItemStack item_pokemon = Utils.getPixelmonItem(pokemon);
		ItemMeta im = item_pokemon.getItemMeta();
		item_pokemon.setAmount((v == 0 ? 1 : v));
		im.setDisplayName("§a§l" + v + "v §e§l" + pokemon_nickname + " §e§l(" + Utils.getPixelmonI18nName(pokemon) +")");
		im.setLore(lore);
		item_pokemon.setItemMeta(im);

		inv.setItem(10, item_pokemon);

		inv.setItem(16, Utils.getItem(Material.EMERALD, 1, 0,
				main.getConfig().getString("decompose-confirm-buttonname").replace("&", "§"), main.getList("decompose-confirm-lore")));

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

		return inv;
	}

	@Override
	public void onInventoryClose(InventoryCloseEvent event) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onInventoryClick(InventoryClickEvent event) {

		Player p = (Player) event.getWhoClicked();
		String prefix = main.getConfig().getString("prefix").replace("&", "§");
		if (event.getInventory().getTitle().equalsIgnoreCase(Utils.fenjie_confirm_title)) {
			event.setCancelled(true);
			p.updateInventory();
			if (event.getRawSlot() == 16) {

				Inventory inv = event.getInventory();
				ItemStack info = inv.getItem(10);
				int slot = Utils.strToInt(info.getItemMeta().getLore().get(0));
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
				int V = Utils.getV(pokemon);
				if(V < main.getMinFenjieV(p)) {
					p.sendMessage(prefix + main.getMessage("fail-4").replace("%v%", String.valueOf(main.getMinFenjieV(p))));
					return;
				}
				String pokemon_name = pokemon.getBaseStats().getPokemonName();
				String pokemon_nickname = pokemon.getNickname() == null ? pokemon.getDisplayName()
						: pokemon.getNickname();
				String pokemon_displayname = pokemon.getDisplayName();
				int amount = main.getConfig().getInt("amount");
				int per = main.getSoulIvs(pokemon.getBaseStats().getPokemonName());
				ItemStack item = new ItemStack(main.getConfig().getInt("soul-item-id"));
				ItemMeta im = item.getItemMeta();
				im.setDisplayName("§1§0§3§e" + pokemon_name + " 的灵魂");
				List<String> lore = new ArrayList<String>();

				for (String s : main.getList("soul-item-lore")) {
					lore.add(s.replace("&", "§").replace("%amount%", "" + amount).replace("%per%", "" + per)
							.replace("%pokemon_name%", pokemon_name)
							.replace("%pokemon_displayname%", pokemon_displayname)
							.replace("%pokemon_nickname%", pokemon_nickname));
				}
				// 使用最后一行来储存每个碎片强化的点数
				// 用黑色来让人变成瞎子看不到数字 ovo
				// 这就是前面强化判断的地方加了清除颜色的原因
				// 而且去颜色也不影响更新前的那些碎片
				// 因此你可以把这里的样式代码改得花里胡哨的 (什么鬼)
				lore.add("§0" + per);

				im.setLore(lore);
				item.setItemMeta(im);
				item.setAmount(amount);
				item.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
				pStorage.set(slot, null);
				p.getInventory().addItem(item);
				p.closeInventory();

				p.sendMessage(prefix + main.getMessage("success-2").replace("&", "§").replace("%amount%", "" + amount)
						.replace("%pokemon_name%", pokemon_name).replace("%pokemon_displayname%", pokemon_displayname)
						.replace("%pokemon_nickname%", pokemon_nickname));

			}
		}
	}
}

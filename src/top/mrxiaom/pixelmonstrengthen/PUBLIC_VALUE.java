package top.mrxiaom.pixelmonstrengthen;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class PUBLIC_VALUE {

	public static String qianghua_title = "§0§0§1§3§l精灵强化";
	public static String fenjie_title = "§0§0§2§c§l请选择你要分解的精灵 [操作不可逆!]";
	public static String fenjie_confirm_title = "§0§0§3§c§l你确定要分解这个精灵吗?";
	public static int ConversionRate = 10;

	public static ItemStack getGlass(int data, String name) {
		return getItem(Material.STAINED_GLASS_PANE, 1, data, name, null);
	}

	public static ItemStack getItem(Material item, int data, String name, List<String> lores) {
		return getItem(item, 1, data, name, lores);
	}

	public static ItemStack getItem(Material item, int count, int data, String name, List<String> lores) {
		ItemStack result = new ItemStack(item);
		ItemMeta im = result.getItemMeta();
		result.setDurability(Short.parseShort("" + data));
		result.setAmount(count);
		im.setDisplayName("§e" + name);
		if (lores != null) {
			im.setLore(lores);
		}
		result.setItemMeta(im);
		return result;
	}
	// 硬核去颜色
	public static String clearColor(String text) {
		return text
				.replace("§a", "")
				.replace("§b", "")
				.replace("§c", "")
				.replace("§d", "")
				.replace("§e", "")
				.replace("§f", "")
				.replace("§r", "")
				.replace("§l", "")
				.replace("§m", "")
				.replace("§n", "")
				.replace("§o", "")
				.replace("§k", "")
				.replace("§0", "")
				.replace("§1", "")
				.replace("§2", "")
				.replace("§3", "")
				.replace("§4", "")
				.replace("§5", "")
				.replace("§6", "")
				.replace("§7", "")
				.replace("§8", "")
				.replace("§9", "")
				.replace("§A", "")
				.replace("§B", "")
				.replace("§C", "")
				.replace("§D", "")
				.replace("§E", "")
				.replace("§F", "")
				.replace("§R", "")
				.replace("§L", "")
				.replace("§M", "")
				.replace("§N", "")
				.replace("§O", "")
				.replace("§K", "");
	}
}

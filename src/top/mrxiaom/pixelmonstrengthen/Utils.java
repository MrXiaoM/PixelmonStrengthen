package top.mrxiaom.pixelmonstrengthen;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.forms.EnumNoForm;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagString;
import net.minecraft.util.text.translation.I18n;

public final class Utils {

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

	public static int getVpoints(int hp,int atk,int def,int s_atk,int s_def,int speed){
		return hp+atk+def+s_atk+s_def+speed;
	}
	
	public static int getV(Pokemon pokemon) {
		return getV(pokemon.getIVs().getStat(StatsType.HP),
				pokemon.getIVs().getStat(StatsType.Attack),
				pokemon.getIVs().getStat(StatsType.Defence),
				pokemon.getIVs().getStat(StatsType.SpecialAttack),
				pokemon.getIVs().getStat(StatsType.SpecialDefence),
				pokemon.getIVs().getStat(StatsType.Speed));
	}
	
	public static int getV(int hp,int atk,int def,int s_atk,int s_def,int speed){
		int v = 0;
		if (hp == 31)
			v++;
		if (atk == 31)
			v++;
		if (def == 31)
			v++;
		if (s_atk == 31)
			v++;
		if (s_def == 31)
			v++;
		if (speed == 31)
			v++;
		return v;
	}

	public static int strToInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
		}
		return -1;
	}
	public static String getPixelmonI18nName(Pokemon pokemon)
	{
		return I18n.func_74838_a("pixelmon." + pokemon.getBaseStats().getPokemonName().toLowerCase() + ".name");
	}

	public static boolean isSuccess(double probability) {
		Random r = new Random();
		if ((double) (r.nextInt(1000) / 10d) <= probability)
			return true;
		else
			return false;
	}

	public static ItemStack getPixelmonItem(Pokemon pokemon)
	{
		
		ItemStack item_pokemon = new ItemStack(Material.valueOf("PIXELMON_PIXELMON_SPRITE"));
		net.minecraft.server.v1_12_R1.ItemStack nmsitem = CraftItemStack.asNMSCopy(item_pokemon);

		String path = "pixelmon:sprites/";
		
		String extra = "";
		Optional<EnumSpecies> opti = EnumSpecies.getFromName(pokemon.getSpecies().name);
		if (opti.isPresent()) {
			EnumSpecies pixelmon = (EnumSpecies) opti.get();
			if (pixelmon.getFormEnum(pokemon.getForm()) != EnumNoForm.NoForm) {
				extra = pixelmon.getFormEnum(pokemon.getForm()).getSpriteSuffix(pokemon.isShiny());
			}

			if (EnumSpecies.mfSprite.contains(pixelmon)) {
				extra = "-" + pokemon.getGender().name().toLowerCase();
			}
			
		}

		path = path + "pokemon/" + pokemon.getSpecies().getNationalPokedexNumber() + extra;

		NBTTagCompound compound = nmsitem.hasTag() ? nmsitem.getTag() : new NBTTagCompound();
		compound.set("SpriteName", new NBTTagString(path));
		nmsitem.setTag(compound);
		
		item_pokemon = CraftItemStack.asBukkitCopy(nmsitem);
		return item_pokemon;
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

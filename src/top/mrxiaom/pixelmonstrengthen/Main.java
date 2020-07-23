package top.mrxiaom.pixelmonstrengthen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EnumSpecialTexture;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.forms.EnumNoForm;
import com.pixelmonmod.pixelmon.enums.forms.EnumPrimal;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagString;
import net.minecraft.util.text.translation.I18n;

public class Main extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	private void refreshTitle() {
		refreshTitle(false);
	}

	private void refreshTitle(boolean shutdown) {
		boolean a = false;
		if (!shutdown) {
			String newvalue1 = "§0§0§1" + this.getConfig().getString("strengthen-title").replace("&", "§");
			String newvalue2 = "§0§0§2" + this.getConfig().getString("decompose-title").replace("&", "§");
			String newvalue3 = "§0§0§3" + this.getConfig().getString("decompose-confirm-title").replace("&", "§");
			
			if (!PUBLIC_VALUE.qianghua_title.equalsIgnoreCase(newvalue1)) {
				PUBLIC_VALUE.qianghua_title = newvalue1;
				a = true;
			}
			if (!PUBLIC_VALUE.fenjie_title.equalsIgnoreCase(newvalue2)) {
				PUBLIC_VALUE.fenjie_title = newvalue2;
				a = true;
			}
			if (!PUBLIC_VALUE.fenjie_title.equalsIgnoreCase(newvalue3)) {
				PUBLIC_VALUE.fenjie_title = newvalue3;
				a = true;
			}
		}
		if (a) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p != null) {
					if (p.getOpenInventory() != null) {
						String title = p.getOpenInventory().getTitle();
						if (title.equalsIgnoreCase(PUBLIC_VALUE.qianghua_title)
								|| title.equalsIgnoreCase(PUBLIC_VALUE.qianghua_title)) {
							p.closeInventory();
							p.sendMessage(this.getConfig().getString("prefix").replace("&", "§")
									+ this.getMessage("reloaded-player"));
						}
					}
				}
			}
		}
	}

	@Override
	public void onDisable() {
		refreshTitle(true);
	}

	private String getMessage(String key) {
		if (this.getConfig().contains("message")) {
			if (this.getConfig().getConfigurationSection("message").contains(key)) {
				return this.getConfig().getConfigurationSection("message").getString(key).replace("&", "§");
			}
		}
		if (this.getConfig().getDefaults().contains("message")) {
			if (this.getConfig().getDefaults().getConfigurationSection("message").contains(key)) {
				return this.getConfig().getDefaults().getConfigurationSection("message").getString(key).replace("&",
						"§");
			}
		}
		return "§c语言键错误: message." + key + " ，请联系管理员";
	}

	private boolean isSuccess(double probability) {
		Random r = new Random();
		if ((double) (r.nextInt(1000) / 10d) <= probability)
			return true;
		else
			return false;
	}

	private List<String> getList(String key) {
		if (this.getConfig().contains(key)) {
			List<String> result = new ArrayList<String>();
			List<String> awa = this.getConfig().getStringList(key);
			for (String s : awa) {
				result.add(s.replace("&", "§"));
			}
			return result;
		}
		return new ArrayList<String>();
	}
	
	private ItemStack getPixelmonItem(Pokemon pokemon)
	{
		ItemStack item_pokemon = new ItemStack(Material.valueOf("PIXELMON_PIXELMON_SPRITE"));
		net.minecraft.server.v1_12_R1.ItemStack nmsitem = CraftItemStack.asNMSCopy(item_pokemon);

		String path = "pixelmon:sprites/";
		EnumSpecialTexture st = pokemon.getSpecialTexture();
		if (pokemon.getFormEnum() instanceof EnumNoForm && pokemon.getFormEnum().isTemporary()) {
			st = EnumSpecialTexture.None;
		}

		if (pokemon.getFormEnum() instanceof EnumPrimal && pokemon.getFormEnum().isTemporary()) {
			st = EnumSpecialTexture.None;
		}

		if (pokemon.isShiny()) {
			path = path + "shiny";
			st = EnumSpecialTexture.None;
		}

		String extra = "";
		Optional<EnumSpecies> opti = EnumSpecies.getFromName(pokemon.getSpecies().name);
		if (opti.isPresent()) {
			EnumSpecies pixelmon = (EnumSpecies) opti.get();
			if (pixelmon.getFormEnum(pokemon.getForm()) != EnumNoForm.NoForm) {
				extra = pixelmon.getFormEnum(pokemon.getForm()).getSpriteSuffix();
			}

			if (EnumSpecies.mfSprite.contains(pixelmon)) {
				extra = "-" + pokemon.getGender().name().toLowerCase();
			}

			if (st.id > 0 && pixelmon.hasSpecialTexture()) {
				extra = "-special";
			}
		}

		path = path + "pokemon/" + pokemon.getSpecies().getNationalPokedexNumber() + extra;

		NBTTagCompound compound = nmsitem.hasTag() ? nmsitem.getTag() : new NBTTagCompound();
		compound.set("SpriteName", new NBTTagString(path));
		nmsitem.setTag(compound);

		item_pokemon = CraftItemStack.asBukkitCopy(nmsitem);
		return item_pokemon;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length >= 1) {
				String prefix = this.getConfig().getString("prefix").replace("&", "§");
				// 强化
				if (args[0].equalsIgnoreCase("qianghua")) {
					if(p.hasPermission("pixelmonstrengthen.qianghua")) {
					if (args.length == 2) {
						PlayerPartyStorage pStorage = Pixelmon.storageManager.getParty(p.getUniqueId());
						if (pStorage == null) {
							p.sendMessage(prefix + this.getMessage("no-team"));
							return true;
						}
						int slot = strToInt(args[1]);
						if (slot < 0 || slot > 6) {
							p.sendMessage(prefix + this.getMessage("no-number"));
							return true;
						}
						Pokemon pokemon = pStorage.get(slot - 1);
						if (pokemon != null) {
							if (pokemon.isEgg() && !this.getConfig().getBoolean("allow-egg")) {
								p.sendMessage(prefix + this.getMessage("not-allow-egg"));
								return true;
							}
							p.closeInventory();
							p.openInventory(this.getQiangHuaGui(pokemon, slot - 1));
						} else {
							p.sendMessage(prefix + this.getMessage("no-pokemon"));
						}
					}
					}
					else
					{
						p.sendMessage(prefix + this.getMessage("no-permission"));
					}
					return true;
				}
				// 分解
				if (args[0].equalsIgnoreCase("fenjie")) {

					if(p.hasPermission("pixelmonstrengthen.fenjie")) {
					PlayerPartyStorage pStorage = Pixelmon.storageManager.getParty(p.getUniqueId());
					if (pStorage == null) {
						p.sendMessage(prefix + this.getMessage("no-team"));
						return true;
					}
					p.closeInventory();
					p.openInventory(this.getFenJieGui(p));
					return true;
					}
					else
					{
						p.sendMessage(prefix + this.getMessage("no-permission"));
					}
				}
				// 重载
				if (args[0].equalsIgnoreCase("reload")) {
					if(p.hasPermission("pixelmonstrengthen.reload")) {
					this.saveDefaultConfig();
					this.reloadConfig();
					this.refreshTitle();
					p.sendMessage(prefix + this.getMessage("reloaded"));
					return true;					
					}
					else
					{
						p.sendMessage(prefix + this.getMessage("no-permission"));
					}
				}
			}
		} else
			sender.sendMessage(this.getMessage("player-only"));
		return true;
	}
	
	private String getPixelmonI18nName(Pokemon pokemon)
	{
		return I18n.func_74838_a("pixelmon." + pokemon.getBaseStats().pixelmonName.toLowerCase() + ".name");
	}

	private Inventory getQiangHuaGui(Pokemon pokemon, int slot) {
		Inventory inv = Bukkit.createInventory(null, 45, PUBLIC_VALUE.qianghua_title);
		ItemStack biankuang = PUBLIC_VALUE.getGlass(7, this.getMessage("frame-title"));
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
		int v = getV(pokemon);
		List<String> lore = new ArrayList<String>();
		String pokemon_nickname = pokemon.getNickname() == null ? pokemon.getDisplayName()
				: pokemon.getNickname();
		
		lore.add("" + slot);
		lore.add("§7===[§b个体值§7]===");
		lore.add("  §a§l" + this.getMessage("soul-type-1") + ": " + pokemon.getIVs().get(StatsType.HP));
		lore.add("  §a§l" + this.getMessage("soul-type-2") + ": " + pokemon.getIVs().get(StatsType.Attack));
		lore.add("  §a§l" + this.getMessage("soul-type-3") + ": " + pokemon.getIVs().get(StatsType.Defence));
		lore.add("  §a§l" + this.getMessage("soul-type-4") + ": " + pokemon.getIVs().get(StatsType.SpecialAttack));
		lore.add("  §a§l" + this.getMessage("soul-type-5") + ": " + pokemon.getIVs().get(StatsType.SpecialDefence));
		lore.add("  §a§l" + this.getMessage("soul-type-6") + ": " + pokemon.getIVs().get(StatsType.Speed));
		ItemStack item_pokemon = this.getPixelmonItem(pokemon);
		ItemMeta im = item_pokemon.getItemMeta();
		item_pokemon.setAmount((v == 0 ? 1 : v));
		im.setDisplayName("§a§l" + v + "v §e§l" + pokemon_nickname + " §e§l(" + getPixelmonI18nName(pokemon) +")");
		im.setLore(lore);
		item_pokemon.setItemMeta(im);

		inv.setItem(10, item_pokemon);

		inv.setItem(15, PUBLIC_VALUE.getItem(Material.PAPER, 1, 0,
				this.getConfig().getString("tips-name").replace("&", "§"), getList("tips-lore")));

		inv.setItem(16, biankuang);
		inv.setItem(17, biankuang);

		inv.setItem(18, biankuang);
		inv.setItem(19, PUBLIC_VALUE.getGlass(10, "↓" + this.getMessage("soul-type-1") + "↓"));
		inv.setItem(20, PUBLIC_VALUE.getGlass(10, "↓" + this.getMessage("soul-type-2") + "↓"));
		inv.setItem(21, PUBLIC_VALUE.getGlass(10, "↓" + this.getMessage("soul-type-3") + "↓"));
		inv.setItem(22, PUBLIC_VALUE.getGlass(10, "↓" + this.getMessage("soul-type-4") + "↓"));
		inv.setItem(23, PUBLIC_VALUE.getGlass(10, "↓" + this.getMessage("soul-type-5") + "↓"));
		inv.setItem(24, PUBLIC_VALUE.getGlass(10, "↓" + this.getMessage("soul-type-6") + "↓"));
		inv.setItem(25, PUBLIC_VALUE.getItem(Material.LIME_SHULKER_BOX, 0,
				this.getConfig().getString("start-name").replace("&", "§"), getList("start-lore")));
		inv.setItem(26, biankuang);

		inv.setItem(27, biankuang);

		inv.setItem(34, biankuang);
		inv.setItem(35, biankuang);

		inv.setItem(36, biankuang);
		inv.setItem(37, biankuang);
		inv.setItem(38, biankuang);
		inv.setItem(39, biankuang);
		inv.setItem(40, biankuang);
		inv.setItem(41, biankuang);
		inv.setItem(42, biankuang);
		inv.setItem(43, biankuang);
		inv.setItem(44, biankuang);

		return inv;
	}

	private Inventory getFenJieQueRenGui(Pokemon pokemon, int slot) {
		Inventory inv = Bukkit.createInventory(null, 27, PUBLIC_VALUE.fenjie_confirm_title);
		ItemStack biankuang = PUBLIC_VALUE.getGlass(14, this.getMessage("frame-title"));
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

		int v = getV(pokemon);
		String pokemon_name = pokemon.getBaseStats().pixelmonName;
		String pokemon_nickname = pokemon.getNickname() == null ? pokemon.getDisplayName()
				: pokemon.getNickname();
		String pokemon_displayname = pokemon.getDisplayName();
		int amount = this.getConfig().getInt("amount");
		int per = this.getConfig().getInt("default");
		if (this.getConfig().getConfigurationSection("special").contains(pokemon_name)) {
			per = this.getConfig().getConfigurationSection("special").getInt(pokemon_name);
		}
		List<String> lore = new ArrayList<String>();
		lore.add("" + slot);
		lore.add("§7===[§b个体值§7]===");
		lore.add("  §a§l" + this.getMessage("soul-type-1") + ": " + pokemon.getIVs().get(StatsType.HP));
		lore.add("  §a§l" + this.getMessage("soul-type-2") + ": " + pokemon.getIVs().get(StatsType.Attack));
		lore.add("  §a§l" + this.getMessage("soul-type-3") + ": " + pokemon.getIVs().get(StatsType.Defence));
		lore.add("  §a§l" + this.getMessage("soul-type-4") + ": " + pokemon.getIVs().get(StatsType.SpecialAttack));
		lore.add("  §a§l" + this.getMessage("soul-type-5") + ": " + pokemon.getIVs().get(StatsType.SpecialDefence));
		lore.add("  §a§l" + this.getMessage("soul-type-6") + ": " + pokemon.getIVs().get(StatsType.Speed));
		lore.add("§e&§分解将获得 " + pokemon_displayname + " 灵魂 *" +amount);
		lore.add("§e每个碎片可增加 " + per+" 点个体值");
		ItemStack item_pokemon = this.getPixelmonItem(pokemon);
		ItemMeta im = item_pokemon.getItemMeta();
		item_pokemon.setAmount((v == 0 ? 1 : v));
		im.setDisplayName("§a§l" + v + "v §e§l" + pokemon_nickname + " §e§l(" + getPixelmonI18nName(pokemon) +")");
		im.setLore(lore);
		item_pokemon.setItemMeta(im);

		inv.setItem(10, item_pokemon);

		inv.setItem(16, PUBLIC_VALUE.getItem(Material.EMERALD, 1, 0,
				this.getConfig().getString("decompose-confirm-buttonname").replace("&", "§"), getList("decompose-confirm-lore")));

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

	private Inventory getFenJieGui(Player p) {
		Inventory inv = Bukkit.createInventory(null, 27, PUBLIC_VALUE.fenjie_title);
		ItemStack biankuang = PUBLIC_VALUE.getGlass(7, this.getMessage("frame-title"));
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
		PlayerPartyStorage pStorage = Pixelmon.storageManager.getParty(p.getUniqueId());
		for (int i = 0; i < 6; i++) {
			Pokemon pokemon = pStorage.get(i);
			
			ItemStack item = (pokemon == null ? new ItemStack(Material.BARRIER) : this.getPixelmonItem(pokemon));
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(
					pokemon == null ? this.getMessage("no-pokemon-item") : ("§e§l" +(pokemon.getNickname() == null ? pokemon.getDisplayName()
							: pokemon.getNickname())+" §e§l("+ getPixelmonI18nName(pokemon) +")"));

			if (pokemon != null) {

				int v = getV(pokemon);
				List<String> lores = new ArrayList<String>();
				int amount = this.getConfig().getInt("amount");
				int per = ((this.getConfig().getConfigurationSection("special")
						.contains(pokemon.getBaseStats().pixelmonName))
								? this.getConfig().getConfigurationSection("special").getInt(
										pokemon.getBaseStats().pixelmonName)
								: this.getConfig().getInt("default"));
				for (String s : this.getConfig().getStringList("decompose-lore")) {
					lores.add(s.replace("&", "§").replace("%amount%", "" + amount).replace("%per%", "" + per));
				}
				item.setAmount(v);
				im.setLore(lores);
			}
			item.setItemMeta(im);
			inv.setItem(10 + i, item);
		}
		return inv;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		PlayerInventory pi = event.getPlayer().getInventory();
		if (event.getInventory().getTitle().equalsIgnoreCase(PUBLIC_VALUE.qianghua_title)) {
			Inventory inv = event.getInventory();
			for (int i = 28; i < 34; i++) {
				if (inv.getItem(i) != null) {
					pi.addItem(inv.getItem(i));
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player == false) {
			return;
		}
		Player p = (Player) event.getWhoClicked();
		String prefix = this.getConfig().getString("prefix").replace("&", "§");
		int Default = this.getConfig().getInt("default");
		/////////////// 强化 ///////////////
		if (event.getInventory().getTitle().equalsIgnoreCase(PUBLIC_VALUE.qianghua_title)) {
			if (event.getRawSlot() <= 27 || (event.getRawSlot() >= 34 && event.getRawSlot() < 45)) {
				event.setCancelled(true);
				p.updateInventory();
			}
			if (event.getRawSlot() == 25) {
				PlayerPartyStorage pStorage = Pixelmon.storageManager.getParty(p.getUniqueId());
				if (pStorage == null) {
					p.closeInventory();
					p.sendMessage(prefix + this.getMessage("no-team"));
					return;
				}
				Inventory inv = event.getInventory();
				ItemStack info = inv.getItem(10);
				int slot = strToInt(info.getItemMeta().getLore().get(0));
				if (slot < 0 || slot > 6) {
					p.closeInventory();
					p.sendMessage(prefix + this.getMessage("unknown-slot"));
					return;
				}
				Pokemon pokemon = pStorage.get(slot);
				int hp = 0;
				int atk = 0;
				int def = 0;
				int s_atk = 0;
				int s_def = 0;
				int speed = 0;
				String name = "§1§0§3§e" + pokemon.getBaseStats().pixelmonName + " 的灵魂";
				double probability_default = this.getConfig().getDouble("probability_default");
				double probability_special = this.getConfig().getDouble("probability_special");
				boolean fail = false;
				if (inv.getItem(28) != null) {
					ItemStack item = inv.getItem(28);
					ItemMeta im = item.getItemMeta();
					if (im.getDisplayName().equalsIgnoreCase(name)) {
						if (im.hasLore() && im.hasEnchant(Enchantment.WATER_WORKER)) {
							int per = strToInt(im.getLore().get(im.getLore().size() - 1));
							if (per > 0) {
								int ivs = pokemon.getIVs().get(StatsType.HP) + per * item.getAmount();
								if (ivs < 32) {
									boolean ok = false;
									if (per == Default) {
										ok = isSuccess(probability_default);
									} else {
										ok = isSuccess(probability_special);
									}
									if (ok) {
										pokemon.getIVs().set(StatsType.HP, ivs);
										hp = per * item.getAmount();
									} else {
										fail = true;
										p.sendMessage(prefix + this.getMessage("soul-disappear").replace("%type%",
												this.getMessage("soul-type-1")));
									}
									inv.setItem(28, null);
								} else {
									pokemon.getIVs().set(StatsType.HP, 31);
									inv.setItem(28, null);
								}
							}
						}
					}
				}
				if (inv.getItem(29) != null) {
					ItemStack item = inv.getItem(29);
					ItemMeta im = item.getItemMeta();
					if (im.getDisplayName().equalsIgnoreCase(name)) {
						if (im.hasLore() && im.hasEnchant(Enchantment.WATER_WORKER)) {
							int per = strToInt(im.getLore().get(im.getLore().size() - 1));
							if (per > 0) {
								int ivs = pokemon.getIVs().get(StatsType.Attack) + per * item.getAmount();
								if (ivs < 32) {
									boolean ok = false;
									if (per == Default) {
										ok = isSuccess(probability_default);
									} else {
										ok = isSuccess(probability_special);
									}
									if (ok) {
										pokemon.getIVs().set(StatsType.Attack, ivs);
										atk = per * item.getAmount();
									} else {
										fail = true;
										p.sendMessage(prefix + this.getMessage("soul-disappear").replace("%type%",
												this.getMessage("soul-type-2")));
									}
									inv.setItem(29, null);
								} else {
									pokemon.getIVs().set(StatsType.Attack, 31);
									inv.setItem(29, null);
								}
							}
						}
					}
				}
				if (inv.getItem(30) != null) {
					ItemStack item = inv.getItem(30);
					ItemMeta im = item.getItemMeta();
					if (im.getDisplayName().equalsIgnoreCase(name)) {
						if (im.hasLore() && im.hasEnchant(Enchantment.WATER_WORKER)) {
							int per = strToInt(im.getLore().get(im.getLore().size() - 1));
							if (per > 0) {
								int ivs = pokemon.getIVs().get(StatsType.Defence) + per * item.getAmount();
								if (ivs < 32) {
									boolean ok = false;
									if (per == Default) {
										ok = isSuccess(probability_default);
									} else {
										ok = isSuccess(probability_special);
									}
									if (ok) {
										pokemon.getIVs().set(StatsType.Defence, ivs);
										def = per * item.getAmount();
									} else {
										fail = true;
										p.sendMessage(prefix + this.getMessage("soul-disappear").replace("%type%",
												this.getMessage("soul-type-3")));
									}
									inv.setItem(30, null);
								} else {
									pokemon.getIVs().set(StatsType.Defence, 31);
									inv.setItem(30, null);
								}
							}
						}
					}
				}
				if (inv.getItem(31) != null) {
					ItemStack item = inv.getItem(31);
					ItemMeta im = item.getItemMeta();
					if (im.getDisplayName().equalsIgnoreCase(name)) {
						if (im.hasLore() && im.hasEnchant(Enchantment.WATER_WORKER)) {
							int per = strToInt(im.getLore().get(im.getLore().size() - 1));
							if (per > 0) {
								int ivs = pokemon.getIVs().get(StatsType.SpecialAttack) + per * item.getAmount();
								if (ivs < 32) {
									boolean ok = false;
									if (per == Default) {
										ok = isSuccess(probability_default);
									} else {
										ok = isSuccess(probability_special);
									}
									if (ok) {
										pokemon.getIVs().set(StatsType.SpecialAttack, ivs);
										s_atk = per * item.getAmount();
									} else {
										fail = true;
										p.sendMessage(prefix + this.getMessage("soul-disappear").replace("%type%",
												this.getMessage("soul-type-4")));
									}
									inv.setItem(31, null);
								} else {
									pokemon.getIVs().set(StatsType.SpecialAttack, 31);
									inv.setItem(31, null);
								}
							}
						}
					}
				}
				if (inv.getItem(32) != null) {
					ItemStack item = inv.getItem(32);
					ItemMeta im = item.getItemMeta();
					if (im.getDisplayName().equalsIgnoreCase(name)) {
						if (im.hasLore() && im.hasEnchant(Enchantment.WATER_WORKER)) {
							int per = strToInt(im.getLore().get(im.getLore().size() - 1));
							if (per > 0) {
								int ivs = pokemon.getIVs().get(StatsType.SpecialDefence) + per * item.getAmount();
								if (ivs < 32) {
									boolean ok = false;
									if (per == Default) {
										ok = isSuccess(probability_default);
									} else {
										ok = isSuccess(probability_special);
									}
									if (ok) {
										pokemon.getIVs().set(StatsType.SpecialDefence, ivs);
										s_def = per * item.getAmount();
									} else {
										fail = true;
										p.sendMessage(prefix + this.getMessage("soul-disappear").replace("%type%",
												this.getMessage("soul-type-5")));
									}
									inv.setItem(32, null);
								} else {
									pokemon.getIVs().set(StatsType.SpecialDefence, 31);
									inv.setItem(32, null);
								}
							}
						}
					}
				}
				if (inv.getItem(33) != null) {
					ItemStack item = inv.getItem(33);
					ItemMeta im = item.getItemMeta();
					if (im.getDisplayName().equalsIgnoreCase(name)) {
						if (im.hasLore() && im.hasEnchant(Enchantment.WATER_WORKER)) {
							int per = strToInt(im.getLore().get(im.getLore().size() - 1));
							if (per > 0) {
								int ivs = pokemon.getIVs().get(StatsType.Speed) + per * item.getAmount();
								if (ivs < 32) {
									boolean ok = false;
									if (per == Default) {
										ok = isSuccess(probability_default);
									} else {
										ok = isSuccess(probability_special);
									}
									if (ok) {
										pokemon.getIVs().set(StatsType.Speed, ivs);
										speed = per * item.getAmount();
									} else {
										fail = true;
										p.sendMessage(prefix + this.getMessage("soul-disappear").replace("%type%",
												this.getMessage("soul-type-6")));
									}
									inv.setItem(33, null);
								} else {
									pokemon.getIVs().set(StatsType.Speed, 31);
									inv.setItem(33, null);
								}
							}
						}
					}
				}
				if (hp != 0 || atk != 0 || def != 0 || s_atk != 0 || s_def != 0 || speed != 0) {
					String situation = (hp != 0 ? (this.getMessage("soul-type-1") + "+" + hp + "， ") : "")
							+ (atk != 0 ? (this.getMessage("soul-type-2") + "+" + atk + "， ") : "")
							+ (def != 0 ? (this.getMessage("soul-type-3") + "+" + def + "， ") : "")
							+ (s_atk != 0 ? (this.getMessage("soul-type-4") + "+" + s_atk + "， ") : "")
							+ (s_def != 0 ? (this.getMessage("soul-type-5") + "+" + s_def + "， ") : "")
							+ (speed != 0 ? (this.getMessage("soul-type-6") + "+" + speed + "") : "");
					p.sendMessage(prefix + this.getMessage("success-1").replace("&", "§").replace("\\n", "\n")
							.replace("%situation%", situation));
				} else {
					if (fail) {
						p.sendMessage(prefix + this.getMessage("fail-1"));
					} else {
						p.sendMessage(prefix + this.getMessage("fail-2"));
					}
				}
				int v = getV(pokemon);
				List<String> lore = new ArrayList<String>();
				lore.add("" + slot);
				lore.add("§7===[§b个体值§7]===");
				lore.add("  §a§l" + this.getMessage("soul-type-1") + ": " + pokemon.getIVs().get(StatsType.HP));
				lore.add("  §a§l" + this.getMessage("soul-type-2") + ": " + pokemon.getIVs().get(StatsType.Attack));
				lore.add("  §a§l" + this.getMessage("soul-type-3") + ": " + pokemon.getIVs().get(StatsType.Defence));
				lore.add("  §a§l" + this.getMessage("soul-type-4") + ": "
						+ pokemon.getIVs().get(StatsType.SpecialAttack));
				lore.add("  §a§l" + this.getMessage("soul-type-5") + ": "
						+ pokemon.getIVs().get(StatsType.SpecialDefence));
				lore.add("  §a§l" + this.getMessage("soul-type-6") + ": " + pokemon.getIVs().get(StatsType.Speed));
				inv.setItem(10, PUBLIC_VALUE.getItem(Material.ITEM_FRAME, (v == 0 ? 1 : v), 0,
						"§a§l" + v + "v §e§l" + pokemon.getDisplayName(), lore));
			}
		}
		/////////////// 分解 ///////////////
		if (event.getInventory().getTitle().equalsIgnoreCase(PUBLIC_VALUE.fenjie_title)) {
			event.setCancelled(true);
			p.updateInventory();
			if (event.getRawSlot() >= 10 && event.getRawSlot() <= 15) {
				int slot = event.getRawSlot() - 10;
				PlayerPartyStorage pStorage = Pixelmon.storageManager.getParty(p.getUniqueId());
				if (pStorage == null) {
					p.closeInventory();
					p.sendMessage(prefix + this.getMessage("no-team"));
					return;
				}
				if (slot < 0 || slot > 6) {
					p.closeInventory();
					p.sendMessage(prefix + this.getMessage("unknown-slot"));
					return;
				}
				Pokemon pokemon = pStorage.get(slot);
				if (pokemon == null) {
					return;
				}
				if (pStorage.countAll() <= 1) {
					p.sendMessage(prefix + this.getMessage("last-pokemon"));
					return;
				}
				p.closeInventory();
				p.openInventory(this.getFenJieQueRenGui(pokemon, slot));
			}
		}
		
		/////////////// 分解确认 ///////////////
		if (event.getInventory().getTitle().equalsIgnoreCase(PUBLIC_VALUE.fenjie_title)) {
			event.setCancelled(true);
			p.updateInventory();
			if (event.getRawSlot() == 16) {

				Inventory inv = event.getInventory();
				ItemStack info = inv.getItem(10);
				int slot = strToInt(info.getItemMeta().getLore().get(0));
				PlayerPartyStorage pStorage = Pixelmon.storageManager.getParty(p.getUniqueId());
				if (pStorage == null) {
					p.closeInventory();
					p.sendMessage(prefix + this.getMessage("no-team"));
					return;
				}
				if (slot < 0 || slot > 6) {
					p.closeInventory();
					p.sendMessage(prefix + this.getMessage("unknown-slot"));
					return;
				}
				Pokemon pokemon = pStorage.get(slot);
				if (pokemon == null) {
					return;
				}
				if (pStorage.countAll() <= 1) {
					p.sendMessage(prefix + this.getMessage("last-pokemon"));
					return;
				}
				String pokemon_name = pokemon.getBaseStats().pixelmonName;
				String pokemon_nickname = pokemon.getNickname() == null ? pokemon.getDisplayName()
						: pokemon.getNickname();
				String pokemon_displayname = pokemon.getDisplayName();
				int amount = this.getConfig().getInt("amount");
				int per = this.getConfig().getInt("default");
				if (this.getConfig().getConfigurationSection("special").contains(pokemon_name)) {
					per = this.getConfig().getConfigurationSection("special").getInt(pokemon_name);
				}
				ItemStack item = new ItemStack(this.getConfig().getInt("soul-item-id"));
				ItemMeta im = item.getItemMeta();
				im.setDisplayName("§1§0§3§e" + pokemon_name + " 的灵魂");
				List<String> lore = new ArrayList<String>();

				for (String s : this.getList("soul-item-lore")) {
					lore.add(s.replace("&", "§").replace("%amount%", "" + amount).replace("%per%", "" + per)
							.replace("%pokemon_name%", pokemon_name)
							.replace("%pokemon_displayname%", pokemon_displayname)
							.replace("%pokemon_nickname%", pokemon_nickname));
				}

				lore.add("" + per);

				im.setLore(lore);
				item.setItemMeta(im);
				item.setAmount(amount);
				item.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
				pStorage.set(slot, null);
				p.getInventory().addItem(item);
				p.closeInventory();

				p.sendMessage(prefix + this.getMessage("success-2").replace("&", "§").replace("%amount%", "" + amount)
						.replace("%pokemon_name%", pokemon_name).replace("%pokemon_displayname%", pokemon_displayname)
						.replace("%pokemon_nickname%", pokemon_nickname));

			}
		}
	}

	private int getV(Pokemon pokemon) {
		int v = 0;
		if (pokemon.getIVs().get(StatsType.HP) == 31)
			v++;
		if (pokemon.getIVs().get(StatsType.Attack) == 31)
			v++;
		if (pokemon.getIVs().get(StatsType.Defence) == 31)
			v++;
		if (pokemon.getIVs().get(StatsType.SpecialAttack) == 31)
			v++;
		if (pokemon.getIVs().get(StatsType.SpecialDefence) == 31)
			v++;
		if (pokemon.getIVs().get(StatsType.Speed) == 31)
			v++;
		return v;
	}

	private int strToInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
		}
		return -1;
	}
}

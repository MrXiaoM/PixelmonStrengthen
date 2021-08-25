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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;

public class GuiQiangHua implements GuiModel{
	Main main;
	public GuiQiangHua(Main main) {
		this.main = main;
	}
	
	@Override
	public Inventory create(Player player, Pokemon pokemon, int intValue) {
		Inventory inv = Bukkit.createInventory(null, 45, Utils.qianghua_title);
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
		int v = Utils.getV(pokemon);
		List<String> lore = new ArrayList<String>();
		String pokemon_nickname = pokemon.getNickname() == null ? pokemon.getDisplayName()
				: pokemon.getNickname();
		
		lore.add("§0" + intValue);
		lore.add("§7===[§b个体值§7]===");
		lore.add("  §a§l" + main.getMessage("soul-type-1") + ": " + pokemon.getIVs().getStat(StatsType.HP));
		lore.add("  §a§l" + main.getMessage("soul-type-2") + ": " + pokemon.getIVs().getStat(StatsType.Attack));
		lore.add("  §a§l" + main.getMessage("soul-type-3") + ": " + pokemon.getIVs().getStat(StatsType.Defence));
		lore.add("  §a§l" + main.getMessage("soul-type-4") + ": " + pokemon.getIVs().getStat(StatsType.SpecialAttack));
		lore.add("  §a§l" + main.getMessage("soul-type-5") + ": " + pokemon.getIVs().getStat(StatsType.SpecialDefence));
		lore.add("  §a§l" + main.getMessage("soul-type-6") + ": " + pokemon.getIVs().getStat(StatsType.Speed));
		ItemStack item_pokemon = Utils.getPixelmonItem(pokemon);
		ItemMeta im = item_pokemon.getItemMeta();
		item_pokemon.setAmount((v == 0 ? 1 : v));
		im.setDisplayName("§a§l" + v + "v §e§l" + pokemon_nickname + " §e§l(" + Utils.getPixelmonI18nName(pokemon) +")");
		im.setLore(lore);
		item_pokemon.setItemMeta(im);

		inv.setItem(10, item_pokemon);

		inv.setItem(15, Utils.getItem(Material.PAPER, 1, 0,
				main.getConfig().getString("tips-name").replace("&", "§"), main.getList("tips-lore")));

		inv.setItem(16, biankuang);
		inv.setItem(17, biankuang);

		inv.setItem(18, biankuang);
		inv.setItem(19, Utils.getGlass(10, "↓" + main.getMessage("soul-type-1") + "↓"));
		inv.setItem(20, Utils.getGlass(10, "↓" + main.getMessage("soul-type-2") + "↓"));
		inv.setItem(21, Utils.getGlass(10, "↓" + main.getMessage("soul-type-3") + "↓"));
		inv.setItem(22, Utils.getGlass(10, "↓" + main.getMessage("soul-type-4") + "↓"));
		inv.setItem(23, Utils.getGlass(10, "↓" + main.getMessage("soul-type-5") + "↓"));
		inv.setItem(24, Utils.getGlass(10, "↓" + main.getMessage("soul-type-6") + "↓"));
		inv.setItem(25, Utils.getItem(Material.LIME_SHULKER_BOX, 0,
				main.getConfig().getString("start-name").replace("&", "§"), main.getList("start-lore")));
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

	@Override
	public void onInventoryClose(InventoryCloseEvent event) {
		PlayerInventory pi = event.getPlayer().getInventory();
		if (event.getInventory().getTitle().equalsIgnoreCase(Utils.qianghua_title)) {
			Inventory inv = event.getInventory();
			for (int i = 28; i < 34; i++) {
				if (inv.getItem(i) != null) {
					pi.addItem(inv.getItem(i));
				}
			}
		}
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {

		Player p = (Player) event.getWhoClicked();
		String prefix = main.getConfig().getString("prefix").replace("&", "§");
		int Default = main.getConfig().getInt("default");

		if (event.getInventory().getTitle().equalsIgnoreCase(Utils.qianghua_title)) {
			if (event.getRawSlot() <= 27 || 
				(event.getRawSlot() >= 34 && event.getRawSlot() < 45)) {
				event.setCancelled(true);
				p.updateInventory();
			}
			if (event.getRawSlot() == 25) {
				// 如果点券或金币不够
				if(main.getQianghuaPoints() > 0 && main.pointsApi.look(p.getUniqueId()) < main.getQianghuaPoints()) {
					p.closeInventory();
					p.sendMessage(prefix + main.getMessage("no-points").replace("%points%", ""+main.getQianghuaPoints()));
					return;
				}
				if(main.getQianghuaMoney() > 0 && main.money.getBalance(p) < main.getQianghuaMoney()) {
					p.closeInventory();
					p.sendMessage(prefix + main.getMessage("no-money").replace("%money%", ""+main.getQianghuaMoney()));
					return;
				}
				PlayerPartyStorage pStorage = Pixelmon.storageManager.getParty(p.getUniqueId());
				if (pStorage == null) {
					p.closeInventory();
					p.sendMessage(prefix + main.getMessage("no-team"));
					return;
				}
				Inventory inv = event.getInventory();
				ItemStack info = inv.getItem(10);
				int slot = Utils.strToInt(Utils.clearColor(info.getItemMeta().getLore().get(0)));
				if (slot < 0 || slot > 6) {
					p.closeInventory();
					p.sendMessage(prefix + main.getMessage("unknown-slot"));
					return;
				}
				Pokemon pokemon = pStorage.get(slot);
				// 精灵原个体值
				int hp = pokemon.getIVs().getStat(StatsType.HP);
				int atk = pokemon.getIVs().getStat(StatsType.Attack);
				int def = pokemon.getIVs().getStat(StatsType.Defence);
				int s_atk = pokemon.getIVs().getStat(StatsType.SpecialAttack);
				int s_def = pokemon.getIVs().getStat(StatsType.SpecialDefence);
				int speed = pokemon.getIVs().getStat(StatsType.Speed);
				// 指定强化完毕后要清空的格子 (避免有异物混入)
				List<Integer> clearSlots = new ArrayList<Integer>();
				// 指定强化完成后强化失败的灵魂类型
				List<Integer> failedTypes = new ArrayList<Integer>();
				// 下面这六个是强化后的个体值变化情况 (+)
				int changing_hp = 0;
				int changing_atk = 0;
				int changing_def = 0;
				int changing_s_atk = 0;
				int changing_s_def = 0;
				int changing_speed = 0;
				
				// 指定正确对应该宝可梦的灵魂碎片名称应该是怎么样的
				String name = "§1§0§3§e" + pokemon.getBaseStats().getPokemonName() + " 的灵魂";
				// 获取配置文件中的成功几率
				double probability_default = main.getConfig().getDouble("probability_default");
				double probability_special = main.getConfig().getDouble("probability_special");
				boolean fail = false;
				///////////////////// 处理灵魂碎片 START /////////////////////
				
				// 生命
				if (inv.getItem(28) != null) {
					ItemStack item = inv.getItem(28);
					ItemMeta im = item.getItemMeta();
					if (im.getDisplayName().equalsIgnoreCase(name)) {
						if (im.hasLore() && im.hasEnchant(Enchantment.WATER_WORKER)) {
							// 获取每个碎片提升的个体值
							int per = Utils.strToInt(Utils.clearColor(im.getLore().get(im.getLore().size() - 1)));
							// 如果要提升的个体值大于0
							if (per > 0) {
								// 计算新的个体 (原个体 + 单个碎片提升的个体值 × 碎片数量)
								int ivs = hp + per * item.getAmount();
								// 如果新的个体大于31
								if (ivs > 31) {
									// 设置为31 (不允许超过31)
									ivs = 31;
								}
								boolean ok = false;
								// 如果单个碎片提升的个体值和默认的相同
								if (per == Default) {
									// 计算是否强化成功 (普通碎片几率)
									ok = Utils.isSuccess(probability_default);
								} else {
									// 计算是否强化成功 (特殊碎片几率)
									ok = Utils.isSuccess(probability_special);
								}
								// 如果成功了
								if (ok) {
									// 设置新的个体值并记录提升的个体信息
									changing_hp = per * item.getAmount();
									if(changing_hp + hp > 31) {
										changing_hp = hp + changing_hp - 31;
									}
									hp = ivs;
								} else { //如果失败了
									// 设置失败标志
									fail = true;
									failedTypes.add(1);
								}
								// 清空该格子
								clearSlots.add(28);
							}
						}
					}
				}
				// 攻击
				if (inv.getItem(29) != null) {
					ItemStack item = inv.getItem(29);
					ItemMeta im = item.getItemMeta();
					if (im.getDisplayName().equalsIgnoreCase(name)) {
						if (im.hasLore() && im.hasEnchant(Enchantment.WATER_WORKER)) {
							// 获取每个碎片提升的个体值
							int per = Utils.strToInt(Utils.clearColor(im.getLore().get(im.getLore().size() - 1)));
							// 如果要提升的个体值大于0
							if (per > 0) {
								// 计算新的个体 (原个体 + 单个碎片提升的个体值 × 碎片数量)
								int ivs = atk + per * item.getAmount();
								// 如果新的个体大于31
								if (ivs > 31) {
									// 设置为31 (不允许超过31)
									ivs = 31;
								}
								boolean ok = false;
								// 如果单个碎片提升的个体值和默认的相同
								if (per == Default) {
									// 计算是否强化成功 (普通碎片几率)
									ok = Utils.isSuccess(probability_default);
								} else {
									// 计算是否强化成功 (特殊碎片几率)
									ok = Utils.isSuccess(probability_special);
								}
								// 如果成功了
								if (ok) {
									// 设置新的个体值并记录提升的个体信息
									changing_atk = per * item.getAmount();
									if(changing_atk + atk > 31) {
										changing_atk = atk + changing_atk - 31;
									}
									atk = ivs;
								} else { //如果失败了
									// 设置失败标志
									fail = true;
									failedTypes.add(2);
								}
								// 清空该格子
								clearSlots.add(29);
							}
						}
					}
				}
				// 防御
				if (inv.getItem(30) != null) {
					ItemStack item = inv.getItem(30);
					ItemMeta im = item.getItemMeta();
					if (im.getDisplayName().equalsIgnoreCase(name)) {
						if (im.hasLore() && im.hasEnchant(Enchantment.WATER_WORKER)) {
							// 获取每个碎片提升的个体值
							int per = Utils.strToInt(Utils.clearColor(im.getLore().get(im.getLore().size() - 1)));
							// 如果要提升的个体值大于0
							if (per > 0) {
								// 计算新的个体 (原个体 + 单个碎片提升的个体值 × 碎片数量)
								int ivs = def + per * item.getAmount();
								// 如果新的个体大于31
								if (ivs > 31) {
									// 设置为31 (不允许超过31)
									ivs = 31;
								}
								boolean ok = false;
								// 如果单个碎片提升的个体值和默认的相同
								if (per == Default) {
									// 计算是否强化成功 (普通碎片几率)
									ok = Utils.isSuccess(probability_default);
								} else {
									// 计算是否强化成功 (特殊碎片几率)
									ok = Utils.isSuccess(probability_special);
								}
								// 如果成功了
								if (ok) {
									// 设置新的个体值并记录提升的个体信息
									changing_def = per * item.getAmount();
									if(changing_def + def > 31) {
										changing_def = def + changing_def - 31;
									}
									def = ivs;
								} else { //如果失败了
									// 设置失败标志
									fail = true;
									failedTypes.add(3);
								}
								// 清空该格子
								clearSlots.add(30);
							}
						}
					}
				}
				// 特攻
				if (inv.getItem(31) != null) {
					ItemStack item = inv.getItem(31);
					ItemMeta im = item.getItemMeta();
					if (im.getDisplayName().equalsIgnoreCase(name)) {
						if (im.hasLore() && im.hasEnchant(Enchantment.WATER_WORKER)) {
							// 获取每个碎片提升的个体值
							int per = Utils.strToInt(Utils.clearColor(im.getLore().get(im.getLore().size() - 1)));
							// 如果要提升的个体值大于0
							if (per > 0) {
								// 计算新的个体 (原个体 + 单个碎片提升的个体值 × 碎片数量)
								int ivs = s_atk + per * item.getAmount();
								// 如果新的个体大于31
								if (ivs > 31) {
									// 设置为31 (不允许超过31)
									ivs = 31;
								}
								boolean ok = false;
								// 如果单个碎片提升的个体值和默认的相同
								if (per == Default) {
									// 计算是否强化成功 (普通碎片几率)
									ok = Utils.isSuccess(probability_default);
								} else {
									// 计算是否强化成功 (特殊碎片几率)
									ok = Utils.isSuccess(probability_special);
								}
								// 如果成功了
								if (ok) {
									// 设置新的个体值并记录提升的个体信息
									changing_s_atk = per * item.getAmount();
									if(changing_s_atk + s_atk > 31) {
										changing_s_atk = s_atk + changing_s_atk - 31;
									}
									s_atk = ivs;
								} else { //如果失败了
									// 设置失败标志
									fail = true;
									failedTypes.add(4);
								}
								// 清空该格子
								clearSlots.add(31);
							}
						}
					}
				}
				// 特防
				if (inv.getItem(32) != null) {
					ItemStack item = inv.getItem(32);
					ItemMeta im = item.getItemMeta();
					if (im.getDisplayName().equalsIgnoreCase(name)) {
						if (im.hasLore() && im.hasEnchant(Enchantment.WATER_WORKER)) {
							// 获取每个碎片提升的个体值
							int per = Utils.strToInt(Utils.clearColor(im.getLore().get(im.getLore().size() - 1)));
							// 如果要提升的个体值大于0
							if (per > 0) {
								// 计算新的个体 (原个体 + 单个碎片提升的个体值 × 碎片数量)
								int ivs = s_def + per * item.getAmount();
								// 如果新的个体大于31
								if (ivs > 31) {
									// 设置为31 (不允许超过31)
									ivs = 31;
								}
								boolean ok = false;
								// 如果单个碎片提升的个体值和默认的相同
								if (per == Default) {
									// 计算是否强化成功 (普通碎片几率)
									ok = Utils.isSuccess(probability_default);
								} else {
									// 计算是否强化成功 (特殊碎片几率)
									ok = Utils.isSuccess(probability_special);
								}
								// 如果成功了
								if (ok) {
									// 设置新的个体值并记录提升的个体信息
									changing_s_def = per * item.getAmount();
									if(changing_s_def + s_def > 31) {
										changing_s_def = s_def + changing_s_def - 31;
									}
									s_def = ivs;
								} else { //如果失败了
									// 设置失败标志
									fail = true;
									failedTypes.add(5);
								}
								// 清空该格子
								clearSlots.add(32);
							}
						}
					}
				}
				// 速度
				if (inv.getItem(33) != null) {
					ItemStack item = inv.getItem(33);
					ItemMeta im = item.getItemMeta();
					if (im.getDisplayName().equalsIgnoreCase(name)) {
						if (im.hasLore() && im.hasEnchant(Enchantment.WATER_WORKER)) {
							// 获取每个碎片提升的个体值
							int per = Utils.strToInt(Utils.clearColor(im.getLore().get(im.getLore().size() - 1)));
							// 如果要提升的个体值大于0
							if (per > 0) {
								// 计算新的个体 (原个体 + 单个碎片提升的个体值 × 碎片数量)
								int ivs = speed + per * item.getAmount();
								// 如果新的个体大于31
								if (ivs > 31) {
									// 设置为31 (不允许超过31)
									ivs = 31;
								}
								boolean ok = false;
								// 如果单个碎片提升的个体值和默认的相同
								if (per == Default) {
									// 计算是否强化成功 (普通碎片几率)
									ok = Utils.isSuccess(probability_default);
								} else {
									// 计算是否强化成功 (特殊碎片几率)
									ok = Utils.isSuccess(probability_special);
								}
								// 如果成功了
								if (ok) {
									// 设置新的个体值并记录提升的个体信息
									changing_speed = per * item.getAmount();
									if(changing_speed + speed > 31) {
										changing_speed = speed + changing_speed - 31;
									}
									speed = ivs;
								} else { //如果失败了
									// 设置失败标志
									fail = true;
									failedTypes.add(6);
								}
								// 清空该格子
								clearSlots.add(33);
							}
						}
					}
				}
				///////////////////// 处理灵魂碎片 END /////////////////////
				
				// 获取新的个体总值
				int newVpoints = Utils.getVpoints(hp,atk,def,s_atk,s_def,speed);
				// 获取新的V数
				int newV = Utils.getV(hp,atk,def,s_atk,s_def,speed);
				
				// 满足以下条件中的其中一个就算失败
				// 1. 新的V数大于限定的
				// 2. 新的个体总值大于限定的
				if(newV > main.getMaxQianghuaV(p) || newVpoints > main.getMaxQianghuaVpoints(p)) {
					// 失败-V超过限定
					if(newV > main.getMaxQianghuaV(p)) {
						p.sendMessage(prefix + main.getMessage("fail-3").replace("\\n", "\n").replace("%v%", ""+main.getMaxQianghuaV(p)));
					// 失败-个体总值超过限定
					}else {
						p.sendMessage(prefix + main.getMessage("fail-3-1").replace("\\n", "\n").replace("%v%", ""+main.getMaxQianghuaVpoints(p)));
					}
					return;
				}
				// 成功
				else {
					// 报告灵魂消失情况并清除格子内容
					for(int i : failedTypes) {
						p.sendMessage(prefix + main.getMessage("soul-disappear").replace("%type%",
								main.getMessage("soul-type-"+i)));
					}
					for(int i : clearSlots) {
						inv.setItem(i, null);
					}
					// 设置新的个体值
					// (newxxx的默认值是原宝可梦的)
					pokemon.getIVs().setStat(StatsType.HP, hp);
					pokemon.getIVs().setStat(StatsType.Attack, atk);
					pokemon.getIVs().setStat(StatsType.Defence, def);
					pokemon.getIVs().setStat(StatsType.SpecialAttack, s_atk);
					pokemon.getIVs().setStat(StatsType.SpecialDefence, s_def);
					pokemon.getIVs().setStat(StatsType.Speed, speed);
				}
				boolean takeTheMoney = false;
				// 害，语言文件就是麻烦
				if (changing_hp != 0 || changing_atk != 0 || changing_def != 0 || changing_s_atk != 0 || changing_s_def != 0 || changing_speed != 0) {
					String situation = (changing_hp != 0 ? (main.getMessage("soul-type-1") + "+" + changing_hp + "， ") : "")
							+ (changing_atk != 0 ? (main.getMessage("soul-type-2") + "+" + changing_atk + "， ") : "")
							+ (changing_def != 0 ? (main.getMessage("soul-type-3") + "+" + changing_def + "， ") : "")
							+ (changing_s_atk != 0 ? (main.getMessage("soul-type-4") + "+" + changing_s_atk + "， ") : "")
							+ (changing_s_def != 0 ? (main.getMessage("soul-type-5") + "+" + changing_s_def + "， ") : "")
							+ (changing_speed != 0 ? (main.getMessage("soul-type-6") + "+" + changing_speed + "") : "");
					p.sendMessage(prefix + main.getMessage("success-1").replace("\\n", "\n")
							.replace("%situation%", situation));
					takeTheMoney = true;
					
				} else {
					if (fail) {
						// 失败原因1: 你灵魂没了
						p.sendMessage(prefix + main.getMessage("fail-1"));
						takeTheMoney = true;
					} else {
						// 失败原因2: 灵魂放置不正确
						p.sendMessage(prefix + main.getMessage("fail-2"));
					}
				}
				if(takeTheMoney) {
					// 取钱走
					if(main.getQianghuaMoney()>0) {
						main.money.withdrawPlayer(p, main.getQianghuaMoney());
					}
					if(main.getQianghuaPoints()>0) {
						main.pointsApi.take(p.getUniqueId(), main.getQianghuaPoints());
					}
				}
				// 刷新菜单中的精灵图标
				int v = Utils.getV(pokemon);
				List<String> lore = new ArrayList<String>();
				lore.add("§0" + slot);
				lore.add("§7===[§b个体值§7]===");
				lore.add("  §a§l" + main.getMessage("soul-type-1") + ": " + pokemon.getIVs().getStat(StatsType.HP));
				lore.add("  §a§l" + main.getMessage("soul-type-2") + ": " + pokemon.getIVs().getStat(StatsType.Attack));
				lore.add("  §a§l" + main.getMessage("soul-type-3") + ": " + pokemon.getIVs().getStat(StatsType.Defence));
				lore.add("  §a§l" + main.getMessage("soul-type-4") + ": " + pokemon.getIVs().getStat(StatsType.SpecialAttack));
				lore.add("  §a§l" + main.getMessage("soul-type-5") + ": " + pokemon.getIVs().getStat(StatsType.SpecialDefence));
				lore.add("  §a§l" + main.getMessage("soul-type-6") + ": " + pokemon.getIVs().getStat(StatsType.Speed));
				ItemStack itemPokemon = inv.getItem(10);
				ItemMeta imPokemon = itemPokemon.getItemMeta();
				imPokemon.setDisplayName("§a§l" + v + "v §e§l" + pokemon.getDisplayName());
				imPokemon.setLore(lore);
				itemPokemon.setAmount((v == 0 ? 1 : v));
				itemPokemon.setItemMeta(imPokemon);
				inv.setItem(10, itemPokemon);
			}
		}
	}
}

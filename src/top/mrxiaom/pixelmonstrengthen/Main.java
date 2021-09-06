package top.mrxiaom.pixelmonstrengthen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener {
	
	PlayerPointsAPI pointsApi;
	Economy money;
	
	private GuiQiangHua guiQiangHua;
	private GuiFenJie guiFenJie;
	private GuiFenJieConfirm guiFenJieConfirm;
	private Map<String, Integer> special = new HashMap<>();
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getLogger().info("正在载入宝可梦强化插件");
		this.getLogger().info("作者QQ: 2431208142 | 如有Bug或建议可加QQ反馈");
		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
	        RegisteredServiceProvider<Economy> serviceProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
	        if (serviceProvider != null) {
	            this.money = serviceProvider.getProvider();
				this.getLogger().info("已找到经济插件 Vault");
	        }
	        else {
				this.getLogger().info("无法勾住经济插件 Vault");
	        }
		}
		else {
			this.getLogger().info("找不到经济插件 Vault");
		}
		if(Bukkit.getPluginManager().getPlugin("PlayerPoints")!=null) {
			PlayerPoints pp = (PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints");
			pointsApi = pp.getAPI();
			this.getLogger().info("已找到点券插件 PlayerPoints");
		}
		else {
			this.getLogger().info("找不到点券插件 PlayerPoints");
		}
		this.guiQiangHua = new GuiQiangHua(this);
		this.guiFenJie = new GuiFenJie(this);
		this.guiFenJieConfirm = new GuiFenJieConfirm(this);
		
	}
	
	public GuiQiangHua getGuiQiangHua() {
		return this.guiQiangHua;
	}
	
	public GuiFenJie getGuiFenJie() {
		return this.guiFenJie;
	}
	
	public GuiFenJieConfirm getGuiFenJieConfirm() {
		return this.guiFenJieConfirm;
	}

	protected void refreshTitle() {
		refreshTitle(false);
	}

	protected void refreshTitle(boolean shutdown) {
		boolean a = false;
		if (!shutdown) {
			String newvalue1 = "§0§0§1" + this.getConfig().getString("strengthen-title").replace("&", "§");
			String newvalue2 = "§0§0§2" + this.getConfig().getString("decompose-title").replace("&", "§");
			String newvalue3 = "§0§0§3" + this.getConfig().getString("decompose-confirm-title").replace("&", "§");
			
			if (!Utils.qianghua_title.equalsIgnoreCase(newvalue1)) {
				Utils.qianghua_title = newvalue1;
				a = true;
			}
			if (!Utils.fenjie_title.equalsIgnoreCase(newvalue2)) {
				Utils.fenjie_title = newvalue2;
				a = true;
			}
			if (!Utils.fenjie_title.equalsIgnoreCase(newvalue3)) {
				Utils.fenjie_title = newvalue3;
				a = true;
			}
		}
		if (a) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p != null) {
					if (p.getOpenInventory() != null) {
						String title = p.getOpenInventory().getTitle();
						if (title.equalsIgnoreCase(Utils.qianghua_title)
								|| title.equalsIgnoreCase(Utils.qianghua_title)) {
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

	protected String getMessage(String key) {
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

	protected List<String> getList(String key) {
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
	
	public void reloadConfig() {
		super.reloadConfig();
		this.special.clear();
		for(String s : this.getConfig().getConfigurationSection("special").getKeys(false)) {
			if(this.getConfig().contains("special." + s) && this.getConfig().isInt("special." + s)) {
				special.put(s.toLowerCase(), this.getConfig().getInt("special." + s));
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String prefix = this.getConfig().getString("prefix").replace("&", "§");
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length >= 1) {
				// 强化
				if (args[0].equalsIgnoreCase("qianghua")) {
					if(!p.hasPermission("pixelmonstrengthen.qianghua")) {
						p.sendMessage(prefix + this.getMessage("no-permission"));
						return true;
					}
					if (args.length == 2) {
						PlayerPartyStorage pStorage = Pixelmon.storageManager.getParty(p.getUniqueId());
						if (pStorage == null) {
							p.sendMessage(prefix + this.getMessage("no-team"));
							return true;
						}
						int slot = Utils.strToInt(args[1]);
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
							p.openInventory(this.getGuiQiangHua().create(null, pokemon, slot - 1));
						} else {
							p.sendMessage(prefix + this.getMessage("no-pokemon"));
						}
					}
					return true;
				}
				// 分解
				if (args[0].equalsIgnoreCase("fenjie")) {

					if(!p.hasPermission("pixelmonstrengthen.fenjie")) {
						p.sendMessage(prefix + this.getMessage("no-permission"));
						return true;
					}
					PlayerPartyStorage pStorage = Pixelmon.storageManager.getParty(p.getUniqueId());
					if (pStorage == null) {
						p.sendMessage(prefix + this.getMessage("no-team"));
						return true;
					}
					p.closeInventory();
					p.openInventory(this.getGuiFenJie().create(p, null, 0));
					return true;
				}
				// 重载
				if (args[0].equalsIgnoreCase("reload")) {

					if(!p.hasPermission("pixelmonstrengthen.reload")) {
						p.sendMessage(prefix + this.getMessage("no-permission"));
						return true;
					}
					this.saveDefaultConfig();
					this.reloadConfig();
					this.refreshTitle();
					p.sendMessage(prefix + this.getMessage("reloaded"));
					return true;
				}
			}
		} else {
			if(sender instanceof ConsoleCommandSender) {
				// 重载
				if (args[0].equalsIgnoreCase("reload")) {
					this.saveDefaultConfig();
					this.reloadConfig();
					this.refreshTitle();
					sender.sendMessage(prefix + this.getMessage("reloaded"));
					return true;
				}
			}
			sender.sendMessage(this.getMessage("player-only"));
		}
		return true;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		this.guiQiangHua.onInventoryClose(event);
		this.guiFenJie.onInventoryClose(event);
		this.guiFenJieConfirm.onInventoryClose(event);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player == false) {
			return;
		}
		this.guiQiangHua.onInventoryClick(event);
		this.guiFenJie.onInventoryClick(event);
		this.guiFenJieConfirm.onInventoryClick(event);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if(event.getMessage().toLowerCase().startsWith("/pixelmonstrengthen") || event.getMessage().toLowerCase().startsWith("/ps")) {
			event.setCancelled(true);
		}
	}
	
	public int getSoulIvs(String pixelmonName) {
		if(special.containsKey(pixelmonName.toLowerCase())) {
			return special.get(pixelmonName.toLowerCase());
		}
		return this.getConfig().getInt("default");
	}

	protected int getQianghuaPoints() {
		if(this.getConfig().contains("strengthen-need-points")){
			return this.getConfig().getInt("strengthen-need-points");
		}
		
		// 如果获取不到，默认0
		return 0;
	}
	
	protected int getQianghuaMoney() {
		if(this.getConfig().contains("strengthen-need-money")){
			return this.getConfig().getInt("strengthen-need-money");
		}
		
		// 如果获取不到，默认0
		return 0;
	}
	
	/* 
	 * 获取玩家最高能强化到多少个体总值
	 * */
	protected int getMaxQianghuaVpoints(Player p) {
		if(this.getConfig().contains("max-ivs")){
			return this.getConfig().getInt("max-ivs");
		}
		
		// 如果获取不到，默认能到最高
		return 186;
	}
	/* 
	 * 获取玩家最高能强化到多少V
	 * */
	protected int getMaxQianghuaV(Player p) {
		for(int i = 6; i > 0; i--) { //从6开始倒数
			// 如果有权限
			if(p.hasPermission("pixelmonstrengthen.qianghua.max." + i)) {
				return i; // 返回值
			}
		}
		// 如果没权限，不允许强化
		return 0;
	}
	
	/* 
	 * 获取玩家分解精灵时最低需要的V数
	 * */
	protected int getMinFenjieV(Player p) {
		for(int i = 0; i < 6; i++) { //从0开始数
			// 如果有权限
			if(p.hasPermission("pixelmonstrengthen.fenjie.min." + i)) {
				return i; // 返回值
			}
		}
		// 如果没权限，得6v才能分解 (亏死)
		return 6;
	}
}

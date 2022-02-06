package top.mrxiaom.pixelmonstrengthen;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import top.mrxiaom.pixelmonstrengthen.utils.Util;

import java.util.logging.Logger;

public class OtherPlugin {
    PlayerPointsAPI pointsApi = null;
    Economy economy = null;
    boolean hasPlayerPoints = false;
    Logger logger;

    public OtherPlugin(Logger logger) {
        this.logger = logger;
        RegisteredServiceProvider<Economy> serviceProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (serviceProvider != null) {
            this.economy = serviceProvider.getProvider();
            this.getLogger().info("已挂钩 Vault 经济服务 " + economy.getName());
        } else {
            this.getLogger().warning("找不到经济服务，请安装一个能与 Vault 挂钩的经济插件");
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Util.hasPAPI = true;
            this.getLogger().info("已将 PlaceholderAPI 作为前置");
        }
        if (Bukkit.getPluginManager().getPlugin("PlayerPoints") != null) {
            hasPlayerPoints = true;
            pointsApi = ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI();
            this.getLogger().info("已将 PlayerPoints 作为前置");
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isPlayerPointsPresent() {
        return hasPlayerPoints;
    }

    public boolean isPlaceholderAPIPresent() {
        return Util.hasPAPI;
    }

    public void giveMoney(Player player, double money) {
        if (economy == null) return;
        economy.depositPlayer(player, money);
    }

    public double getMoney(Player player) {
        if (economy == null) return 0;
        return economy.getBalance(player);
    }

    public boolean hasMoney(Player player, double money) {
        return getMoney(player) >= money;
    }

    public void takeMoney(Player player, double money) {
        if (economy == null) return;
        economy.withdrawPlayer(player, money);
    }

    public void givePoints(Player player, int points) {
        if (!hasPlayerPoints) return;
        pointsApi.give(player.getUniqueId(), points);
    }

    public int getPoints(Player player) {
        if (!hasPlayerPoints) return 0;
        return pointsApi.look(player.getUniqueId());
    }

    public boolean hasPoints(Player player, int points) {
        return getPoints(player) >= points;
    }

    public void takePoints(Player player, int points) {
        if (!hasPlayerPoints) return;
        pointsApi.take(player.getUniqueId(), points);
    }
}

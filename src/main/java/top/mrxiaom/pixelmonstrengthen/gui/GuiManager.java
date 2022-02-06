package top.mrxiaom.pixelmonstrengthen.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class GuiManager implements Listener {
    Plugin plugin;
    Map<Player, IGui> players = new HashMap<>();

    public GuiManager(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void openGui(IGui gui) {
        gui.getPlayer().closeInventory();
        Inventory inv = gui.create();
        if (inv != null) {
            gui.getPlayer().openInventory(inv);
            players.put(gui.getPlayer(), gui);
        } else players.remove(gui.getPlayer());
    }

    public IGui getPlayerGui(Player player) {
        return players.getOrDefault(player, null);
    }

    public void closeAllGui() {
        for (Player player : players.keySet()) {
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (!players.containsKey(player)) return;
        IGui gui = players.get(player);
        gui.onClick(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        if (!players.containsKey(player)) return;
        IGui gui = players.get(player);
        gui.onClose(event);
        players.remove(player);
    }
}

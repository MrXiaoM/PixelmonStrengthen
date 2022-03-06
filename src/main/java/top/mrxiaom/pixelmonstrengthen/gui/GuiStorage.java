package top.mrxiaom.pixelmonstrengthen.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import top.mrxiaom.pixelmonstrengthen.Config;
import top.mrxiaom.pixelmonstrengthen.Lang;
import top.mrxiaom.pixelmonstrengthen.PixelmonStrengthen;
import top.mrxiaom.pixelmonstrengthen.PlayerData;
import top.mrxiaom.pixelmonstrengthen.utils.ItemStackUtil;
import top.mrxiaom.pixelmonstrengthen.utils.Util;

import java.util.Map;
import java.util.Optional;

public class GuiStorage implements IGui {
    PixelmonStrengthen plugin;
    Player player;
    int page = 1;
    int pages = 1;
    PlayerData.Data data;
    public GuiStorage(PixelmonStrengthen plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Inventory create() {
        Inventory inv = Bukkit.createInventory(null, 54, Lang.get("gui.storage.title").replace("%page%", String.valueOf(page)));
        Optional<PlayerData.Data> result = plugin.getPlayerData().get(player.getName());
        if (result.isPresent()) {
            data = result.get();
            Map<String, Integer> storage = data.getStorage();
            pages = storage.size() / 45;
            int i = 0, j = 0;
            for (String id : storage.keySet()) {
                if (j > (page - 1) * 45) {
                    if (j > page * 45) break;
                    Config.PokemonSettings settings = plugin.getPluginConfig().getPokemonSettings(id);
                    inv.setItem(i, plugin.getPluginConfig().getPokemonSoul(player, id, Lang.getPokemonTranslateName(id), settings.ivs, storage.get(id), true));
                    i++;
                }
                j++;
            }
        }

        ItemStackUtil.setItemInLine(inv, Lang.getItem("gui.storage.items.frame", player), 6);
        if (page > 1) ItemStackUtil.setItemIn(inv, Lang.getItem("gui.storage.items.prev-page", player), 45);
        if (page < pages) ItemStackUtil.setItemIn(inv, Lang.getItem("gui.storage.items.next-page", player), 53);
        ItemStackUtil.setItemIn(inv, Lang.getItem("gui.storage.items.custom", player), 49);
        return inv;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        int slot = event.getRawSlot();
        if (slot < 45) {

        }
        // 上一页
        if (page > 1 && slot == 45) {
            page--;
            refresh();
        }
        // 下一页
        if (page < pages && slot == 53) {
            page++;
            refresh();
        }
        if (slot == 49) {
            Util.runCommands(Lang.getList("gui.storage.items.custom.commands", false), player);
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        // do nothing
    }
}

package top.mrxiaom.pixelmonstrengthen.gui;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.pixelmonstrengthen.Config;
import top.mrxiaom.pixelmonstrengthen.Lang;
import top.mrxiaom.pixelmonstrengthen.PixelmonStrengthen;
import top.mrxiaom.pixelmonstrengthen.utils.pixelmonmod.IModSupport;
import top.mrxiaom.pixelmonstrengthen.utils.ItemStackUtil;

public class GuiStrengthSelect implements IGui {
    Player player;

    public GuiStrengthSelect(Player player) {
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Inventory create() {
        Inventory inv = Bukkit.createInventory(null, 27, Lang.get("gui.strength-select.title"));
        ItemStackUtil.setItemAroundInv(inv, Lang.getItem("gui.strength-select.items.frame", player));
        IModSupport mod = PixelmonStrengthen.getInstance().getModSupport();
        PlayerPartyStorage party = mod.getPokemonParty(player.getUniqueId());
        if (party != null) {
            Config config = PixelmonStrengthen.getInstance().getPluginConfig();
            for (int i = 0; i < 6; i++) {
                Pokemon pokemon = party.get(i);
                if (pokemon == null) continue;
                if (!config.isAllowEgg() && pokemon.isEgg()) continue;
                if (!config.checkPermission(player, pokemon)) continue;
                ItemStack item = Lang.getItem("gui.strength-select.items.pokemon", player, Lang.getPokemonLoreReplaceList(pokemon));
                if (item.getType().name().equalsIgnoreCase("PIXELMON_PIXELMON_SPRITE")) {
                    item = mod.toPokemonPhoto(item, pokemon);
                }
                inv.setItem(10 + i, item);
            }
        }
        inv.setItem(16, Lang.getItem("gui.strength-select.items.tips", player));
        return inv;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        int slot = event.getRawSlot();
        if (slot > 9 && slot < 16) {
            PlayerPartyStorage party = PixelmonStrengthen.getInstance().getModSupport().getPokemonParty(player.getUniqueId());
            if (party == null) {
                player.sendMessage(Lang.get("errors.no-party", true));
                return;
            }
            slot -= 10;
            Pokemon pokemon = party.get(slot);
            if (pokemon == null) {
                player.sendMessage(Lang.get("errors.no-pokemon", true));
                return;
            }
            if (!PixelmonStrengthen.getInstance().getPluginConfig().isAllowEgg() && pokemon.isEgg()) {
                player.sendMessage(Lang.get("errors.disallow-egg", true));
                return;
            }
            PixelmonStrengthen.getInstance().getGuiManager().openGui(new GuiStrength(player, slot));
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        // do nothing
    }
}

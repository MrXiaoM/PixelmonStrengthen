package top.mrxiaom.pixelmonstrengthen.gui;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.pixelmonstrengthen.Lang;
import top.mrxiaom.pixelmonstrengthen.PixelmonStrengthen;
import top.mrxiaom.pixelmonstrengthen.utils.Pair;
import top.mrxiaom.pixelmonstrengthen.utils.pixelmonmod.IModSupport;
import top.mrxiaom.pixelmonstrengthen.utils.ItemStackUtil;
import top.mrxiaom.pixelmonstrengthen.utils.Util;

import java.util.List;

public class GuiDecomposePokemonConfirm implements IGui {
    Player player;
    int slot;

    public GuiDecomposePokemonConfirm(Player player, int slot) {
        this.player = player;
        this.slot = slot;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Inventory create() {
        PlayerPartyStorage party = PixelmonStrengthen.getInstance().getModSupport().getPokemonParty(player.getUniqueId());
        if (party == null) {
            player.sendMessage(Lang.get("errors.no-party", true));
            return null;
        }
        Pokemon pokemon = party.get(slot);
        if (pokemon == null) {
            player.sendMessage(Lang.get("errors.no-pokemon", true));
            return null;
        }
        if (!PixelmonStrengthen.getInstance().getPluginConfig().isAllowEgg() && pokemon.isEgg()) {
            player.sendMessage(Lang.get("errors.disallow-egg", true));
            return null;
        }
        Inventory inv = Bukkit.createInventory(null, 27, Lang.get("gui.decompose-confirm.title"));
        ItemStackUtil.setItemAroundInv(inv, Lang.getItem("gui.decompose-confirm.items.frame", player));
        List<Pair<String, String>> replaceList = Lang.getPokemonLoreReplaceList(pokemon);
        replaceList.add(Pair.of("%amount%", String.valueOf(PixelmonStrengthen.getInstance().getPluginConfig().getPokemonSettings(pokemon).amount)));
        inv.setItem(10, Lang.getItem("gui.decompose-confirm.items.pokemon", player, replaceList));

        inv.setItem(16, Lang.getItem("gui.decompose-confirm.items.confirm", player));
        return inv;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        IModSupport mod = PixelmonStrengthen.getInstance().getModSupport();
        PlayerPartyStorage party = mod.getPokemonParty(player.getUniqueId());
        if (party == null) {
            player.sendMessage(Lang.get("errors.no-party", true));
            player.closeInventory();
            return;
        }
        Pokemon pokemon = party.get(slot);
        if (pokemon == null) {
            player.sendMessage(Lang.get("errors.no-pokemon", true));
            player.closeInventory();
            return;
        }
        if (!PixelmonStrengthen.getInstance().getPluginConfig().isAllowEgg() && pokemon.isEgg()) {
            player.sendMessage(Lang.get("errors.disallow-egg", true));
            player.closeInventory();
            return;
        }
        // 确认分解
        if (event.getRawSlot() == 16) {
            ItemStack item = PixelmonStrengthen.getInstance().getPluginConfig().getPokemonSoul(player, pokemon);
            party.set(slot, null);
            Util.giveItemsToPlayer(player, item);
            player.closeInventory();
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}

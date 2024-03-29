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
import top.mrxiaom.pixelmonstrengthen.utils.Pair;
import top.mrxiaom.pixelmonstrengthen.utils.pixelmonmod.IModSupport;
import top.mrxiaom.pixelmonstrengthen.utils.ItemStackUtil;

import java.util.List;

public class GuiDecomposePokemon implements IGui {
    Player player;

    public GuiDecomposePokemon(Player player) {
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Inventory create() {
        Inventory inv = Bukkit.createInventory(null, 27, Lang.get("gui.decompose.title"));
        ItemStackUtil.setItemAroundInv(inv, Lang.getItem("gui.decompose.items.frame", player));
        IModSupport mod = PixelmonStrengthen.getInstance().getModSupport();
        PlayerPartyStorage party = mod.getPokemonParty(player.getUniqueId());
        if (party != null) {
            Config config = PixelmonStrengthen.getInstance().getPluginConfig();
            for (int i = 0; i < 6; i++) {
                Pokemon pokemon = party.get(i);
                if (pokemon == null) continue;
                if (!config.isAllowEgg() && pokemon.isEgg()) continue;
                if (!config.checkPermission(player, pokemon)) continue;
                Config.PokemonSettings settings = config.getPokemonSettings(pokemon);
                List<Pair<String, String>> replaceList = Lang.getPokemonLoreReplaceList(pokemon);
                replaceList.add(Pair.of("%per%", String.valueOf(settings.ivs)));
                replaceList.add(Pair.of("%amount%", String.valueOf(settings.amount)));
                ItemStack item = Lang.getItem("gui.decompose.items.pokemon", player, Lang.getPokemonLoreReplaceList(pokemon));
                if (item.getType().name().equalsIgnoreCase("PIXELMON_PIXELMON_SPRITE")) {
                    item = mod.toPokemonPhoto(item, pokemon);
                }
                inv.setItem(10 + i, item);
            }
        }
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
            PixelmonStrengthen.getInstance().getGuiManager().openGui(new GuiDecomposePokemonConfirm(player, slot));
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        // do nothing
    }
}

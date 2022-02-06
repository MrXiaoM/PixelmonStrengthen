package top.mrxiaom.pixelmonstrengthen;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.mrxiaom.pixelmonstrengthen.gui.GuiDecomposePokemon;
import top.mrxiaom.pixelmonstrengthen.gui.GuiDecomposePokemonConfirm;
import top.mrxiaom.pixelmonstrengthen.gui.GuiStrength;
import top.mrxiaom.pixelmonstrengthen.gui.GuiStrengthSelect;
import top.mrxiaom.pixelmonstrengthen.utils.ItemStackUtil;
import top.mrxiaom.pixelmonstrengthen.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class Commands implements TabCompleter, CommandExecutor {
    PixelmonStrengthen plugin;

    public Commands(PixelmonStrengthen plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("pixelmonstrengthen");
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean isPlayer = sender instanceof Player;
        Player player = isPlayer ? (Player) sender : null;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("strength") || args[0].equalsIgnoreCase("qianghua")) {
                if (!isPlayer) return Lang.notPlayer(sender);
                if (!player.hasPermission("pixelmonstrengthen.strength")) return Lang.noPerm(player);
                if (args.length == 2) {
                    int slot = Util.parseInt(args[1], 0) - 1;
                    if (slot < 1 || slot > 6) {
                        player.sendMessage(Lang.get("errors.not-slot", true));
                        return true;
                    }
                    PlayerPartyStorage party = plugin.getModSupport().getPokemonParty(player.getUniqueId());
                    if (party == null) {
                        player.sendMessage(Lang.get("errors.no-party", true));
                        return true;
                    }
                    Pokemon pokemon = party.get(slot);
                    if (pokemon == null) {
                        player.sendMessage(Lang.get("errors.no-pokemon", true));
                        return true;
                    }
                    if (!plugin.getPluginConfig().checkPermission(player, pokemon)) return Lang.noPerm(player);
                    plugin.getGuiManager().openGui(new GuiStrength(player, slot));
                    return true;
                }
                plugin.getGuiManager().openGui(new GuiStrengthSelect(player));
                return true;
            }
            if (args[0].equalsIgnoreCase("decompose") || args[0].equalsIgnoreCase("fenjie")) {
                if (!isPlayer) return Lang.notPlayer(sender);
                if (!player.hasPermission("pixelmonstrengthen.decompose")) return Lang.noPerm(player);
                if (args.length == 2) {
                    int slot = Util.parseInt(args[1], 0) - 1;
                    if (slot < 1 || slot > 6) {
                        player.sendMessage(Lang.get("errors.not-slot", true));
                        return true;
                    }
                    PlayerPartyStorage party = plugin.getModSupport().getPokemonParty(player.getUniqueId());
                    if (party == null) {
                        player.sendMessage(Lang.get("errors.no-party", true));
                        return true;
                    }
                    Pokemon pokemon = party.get(slot);
                    if (pokemon == null) {
                        player.sendMessage(Lang.get("errors.no-pokemon", true));
                        return true;
                    }
                    if (!plugin.getPluginConfig().checkPermission(player, pokemon)) return Lang.noPerm(player);
                    plugin.getGuiManager().openGui(new GuiDecomposePokemonConfirm(player, slot));
                    return true;
                }
                plugin.getGuiManager().openGui(new GuiDecomposePokemon(player));
                return true;
            }
            if (args[0].equalsIgnoreCase("old")) {
                if (!isPlayer) return Lang.notPlayer(sender);
                if (!player.hasPermission("pixelmonstrengthen.old")) return Lang.noPerm(player);
                // 从旧灵魂转换到新灵魂
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item != null && !item.getType().equals(Material.AIR)) {
                    ItemMeta im = ItemStackUtil.getItemMeta(item);
                    if (im.getDisplayName() != null && im.getDisplayName().startsWith("§1§0§3§e")) {
                        List<String> lore = im.getLore();
                        if (lore != null && !lore.isEmpty()) {
                            int ivs = Util.parseInt(Util.clearColor(lore.get(lore.size() - 1)), -1);
                            if (ivs > 0) {
                                String baseName = im.getDisplayName().substring(8);
                                if (baseName.contains(" ")) {
                                    int amount = item.getAmount();
                                    baseName = baseName.substring(0, baseName.indexOf(" "));
                                    ItemStack finalItem = plugin.getPluginConfig().getPokemonSoul(player, baseName, ivs, amount);
                                    player.getInventory().setItemInMainHand(finalItem);
                                    player.sendMessage(Lang.get("old-converted", true).replace("%amount%", String.valueOf(amount)));
                                    return true;
                                }
                            }
                        }
                    }
                }
                player.sendMessage(Lang.get("errors.old_wrong-item", true));
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("pixelmonstrengthen.reload")) return Lang.noPerm(sender);
                plugin.getPluginConfig().reloadConfig();
                sender.sendMessage(Lang.get("reload", true));
                return true;
            }
            if (args[0].equalsIgnoreCase("debug") && isPlayer && player.isOp()) {
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("pokemon")) {
                        if (args.length == 3) {
                            int slot = Util.parseInt(args[2], 0) - 1;
                            if (slot < 1 || slot > 6) {
                                player.sendMessage(Lang.get("errors.not-slot", true));
                                return true;
                            }
                            PlayerPartyStorage party = plugin.getModSupport().getPokemonParty(player.getUniqueId());
                            if (party == null) {
                                player.sendMessage(Lang.get("errors.no-party", true));
                                return true;
                            }
                            Pokemon pokemon = party.get(slot);
                            if (pokemon == null) {
                                player.sendMessage(Lang.get("errors.no-pokemon", true));
                                return true;
                            }
                            player.sendMessage("baseName: " + plugin.getModSupport().getPokemonBaseName(pokemon));
                            player.sendMessage("translateName: " + Lang.getPokemonTranslateName(pokemon));
                            player.sendMessage("displayName: " + (pokemon.getNickname() != null ? pokemon.getNickname() : Lang.getPokemonTranslateName(pokemon)));
                            return true;
                        }
                        player.sendMessage("查看宝可梦名称: /ps debug pokemon <格数>");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("material")) {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item == null || item.getType().equals(Material.AIR)) {
                            player.sendMessage("请手持一件物品");
                            return true;
                        }
                        player.sendMessage("material: " + item.getType().name().toLowerCase());
                        player.sendMessage("typeId: " + item.getTypeId());
                        player.sendMessage("data: " + item.getDurability());
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("depends")) {
                        plugin.otherPlugin = new OtherPlugin(plugin.getLogger());
                        player.sendMessage("操作完成，详见控制台");
                        return true;
                    }
                }
                player.sendMessage("调试命令 帮助");
                player.sendMessage("查看宝可梦名称: /ps debug pokemon <格数>");
                player.sendMessage("查看主手中的物品ID: /ps debug material");
                player.sendMessage("重新检查前置插件: /ps debug depends");
                return true;
            }
        }
        sender.sendMessage(Lang.getList("help", true));
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list1 = Lists.newArrayList("strength", "decompose", "qianghua", "fenjie");
        if (sender.isOp()) list1.addAll(Lists.newArrayList("reload", "debug"));
        if (args.length == 0) {
            return list1;
        }
        if (sender instanceof Player && sender.isOp() && args[0].equalsIgnoreCase("debug")) {
            return Lists.newArrayList("pokemon", "material");
        }
        return new ArrayList<>();
    }
}

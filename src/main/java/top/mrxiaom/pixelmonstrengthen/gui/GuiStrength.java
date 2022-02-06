package top.mrxiaom.pixelmonstrengthen.gui;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.pixelmonstrengthen.Config;
import top.mrxiaom.pixelmonstrengthen.Lang;
import top.mrxiaom.pixelmonstrengthen.OtherPlugin;
import top.mrxiaom.pixelmonstrengthen.PixelmonStrengthen;
import top.mrxiaom.pixelmonstrengthen.utils.pixelmonmod.IModSupport;
import top.mrxiaom.pixelmonstrengthen.utils.ItemStackUtil;
import top.mrxiaom.pixelmonstrengthen.utils.Pair;
import top.mrxiaom.pixelmonstrengthen.utils.Util;

import java.util.*;

public class GuiStrength implements IGui {
    Player player;
    int slot;
    Inventory inventory;
    Pokemon pokemon;

    public GuiStrength(Player player, int slot) {
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
        this.pokemon = pokemon;
        Inventory inv = Bukkit.createInventory(null, 54, Lang.get("gui.strength.title"));
        ItemStack frame = Lang.getItem("gui.strength.items.frame", player);
        ItemStackUtil.setItemAroundInv(inv, frame);
        ItemStackUtil.setItemInLine(inv, frame, 5);
        ItemStackUtil.setItemIn(inv, frame, 34);
        inv.setItem(10, Lang.getItem("gui.strength.items.pokemon", player, Lang.getPokemonLoreReplaceList(pokemon)));

        inv.setItem(16, Lang.getItem("gui.strength.items.tips", player));
        updateStartButton();

        inv.setItem(19, Lang.getItem("gui.strength.items.frame-ivs-hp", player));
        inv.setItem(20, Lang.getItem("gui.strength.items.frame-ivs-attack", player));
        inv.setItem(21, Lang.getItem("gui.strength.items.frame-ivs-defence", player));
        inv.setItem(22, Lang.getItem("gui.strength.items.frame-ivs-specialattack", player));
        inv.setItem(23, Lang.getItem("gui.strength.items.frame-ivs-specialdefence", player));
        inv.setItem(24, Lang.getItem("gui.strength.items.frame-ivs-speed", player));
        Bukkit.getScheduler().runTaskTimer(PixelmonStrengthen.getInstance(), this::updateStartButton, 10L, 10L);
        return inventory = inv;
    }


    private void updateStartButton() {
        FinalResult result = FinalResult.get(inventory, pokemon, true);
        inventory.setItem(25, Lang.getItem("gui.strength.items.start", player, Lists.newArrayList(
                Pair.of("%money%", String.valueOf(result.finalMoney)),
                Pair.of("%points%", String.valueOf(result.finalPoints))
        )));
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        IModSupport mod = PixelmonStrengthen.getInstance().getModSupport();
        PlayerPartyStorage party = mod.getPokemonParty(player.getUniqueId());
        if (party == null) {
            player.sendMessage(Lang.get("errors.no-party", true));
            event.setCancelled(true);
            player.closeInventory();
            return;
        }
        Pokemon pokemon = party.get(slot);
        if (pokemon == null) {
            player.sendMessage(Lang.get("errors.no-pokemon", true));
            event.setCancelled(true);
            player.closeInventory();
            return;
        }
        if (!PixelmonStrengthen.getInstance().getPluginConfig().isAllowEgg() && pokemon.isEgg()) {
            player.sendMessage(Lang.get("errors.disallow-egg", true));
            player.closeInventory();
            return;
        }
        int slot = event.getRawSlot();
        // 设置可点击背包或者放置灵魂的格子
        if (slot < 28 || (slot > 33 && slot < 54)) {
            event.setCancelled(true);
        }
        // 检查放置灵魂的格子
        if (slot >= 28 && slot <= 33) {
            ItemStack cursor = event.getCursor();
            if (cursor == null || cursor.getType().equals(Material.AIR)) return;
            if (!PixelmonStrengthen.getInstance().checkSoul(cursor, pokemon).isPresent()) {
                player.sendMessage(Lang.get("errors.invalid-soul", true));
                event.setCancelled(true);
            }
            return;
        }
        // 开始强化
        if (slot == 25) {
            Config config = PixelmonStrengthen.getInstance().getPluginConfig();
            Config.PokemonSettings settings = config.getPokemonSettings(pokemon);
            OtherPlugin api = PixelmonStrengthen.getInstance().getOtherPlugin();
            Map<IModSupport.IvsEvsStats, Integer> oldIvs = mod.getIvs(pokemon);
            // 计算结果
            FinalResult result = FinalResult.get(event.getInventory(), pokemon);
            // 算账
            if (settings.useMoney > 0 && !api.hasMoney(player, result.finalMoney)) {
                player.sendMessage(Lang.get("errors.no-money", true));
                return;
            }
            if (settings.usePoints > 0 && !api.hasPoints(player, result.finalPoints)) {
                player.sendMessage(Lang.get("errors.no-points", true));
                return;
            }
            // 扣钱
            if (settings.useMoney > 0) {
                api.takeMoney(player, settings.useMoney);
            }
            if (settings.usePoints > 0) {
                api.takePoints(player, settings.usePoints);
            }
            for (int i : result.finalItems.keySet()) {
                event.getInventory().setItem(i, result.finalItems.get(i));
            }
            // 应用结果到宝可梦
            boolean isSuccess = !result.finalIvsMap.isEmpty();
            if (isSuccess) {
                for (IModSupport.IvsEvsStats type : result.finalIvsMap.keySet()) {
                    mod.setIvs(pokemon, type, result.finalIvsMap.get(type));
                }
            }
            player.closeInventory();
            // 变量
            List<Pair<String, String>> replaceList = Lists.newArrayList(
                    Pair.of("%pokemon_display%", pokemon.getDisplayName()),
                    Pair.of("%pokemon_name%", Lang.getPokemonTranslateName(pokemon)),
                    Pair.of("%pokemon_basename%", mod.getPokemonBaseName(pokemon)),
                    Pair.of("%money%", String.valueOf(result.finalMoney)),
                    Pair.of("%points%", String.valueOf(result.finalPoints))
            );
            int oldTotalIvs = 0;
            int oldV = 0;
            int newTotalIvs = 0;
            int newV = 0;
            int totalSuccessSouls = 0;
            int totalSouls = 0;
            for (IModSupport.IvsEvsStats stat : oldIvs.keySet()) {
                int oldValue = oldIvs.get(stat);
                int newValue = result.finalIvsMap.getOrDefault(stat, oldIvs.get(stat));
                int changedValue = newValue - oldValue;
                oldTotalIvs += oldValue;
                newTotalIvs += newValue;
                if (oldValue == 31) oldV++;
                if (newValue == 31) newV++;
                String statName = stat.name().toLowerCase();
                replaceList.add(Pair.of("%pokemon_old_ivs_" + statName + "%", String.valueOf(oldValue)));
                replaceList.add(Pair.of("%pokemon_new_ivs_" + statName + "%", String.valueOf(newValue)));
                replaceList.add(Pair.of("%pokemon_changed_ivs_" + statName + "%", (changedValue >= 0 ? "+" : "") + changedValue));
                Pair<Integer, Integer> pair = result.soulsMap.get(stat);
                totalSuccessSouls += pair.getKey();
                totalSouls += pair.getValue();
                replaceList.add(Pair.of("%soul_success_" + statName + "%", String.valueOf(pair.getKey())));
                replaceList.add(Pair.of("%soul_total_" + statName + "%", String.valueOf(pair.getValue())));
            }
            int changedV = newV - oldV;
            int changedTotalIvs = newTotalIvs - oldTotalIvs;
            replaceList.add(Pair.of("%pokemon_old_ivs%", String.valueOf(oldTotalIvs)));
            replaceList.add(Pair.of("%pokemon_new_ivs%", String.valueOf(newTotalIvs)));
            replaceList.add(Pair.of("%pokemon_changed_ivs%", (changedTotalIvs >= 0 ? "+" : "") + changedTotalIvs));
            replaceList.add(Pair.of("%pokemon_old_v%", String.valueOf(oldV)));
            replaceList.add(Pair.of("%pokemon_new_v%", String.valueOf(newV)));
            replaceList.add(Pair.of("%pokemon_changed_v%", (changedV >= 0 ? "+" : "") + changedV));
            replaceList.add(Pair.of("%soul_success%", String.valueOf(totalSuccessSouls)));
            replaceList.add(Pair.of("%soul_total%", String.valueOf(totalSouls)));
            boolean isExecuteBeforeMessage = config.isExecuteBeforeMessage();
            if (isExecuteBeforeMessage) {
                Util.runCommands(isSuccess ? config.getStrengthSuccessCommands() : config.getTotallyFailCommands(), player);
            }
            player.sendMessage(Lang.getList(isSuccess ? "strength.success" : "strength.fail", true, replaceList));
            if (!isExecuteBeforeMessage) {
                Util.runCommands(isSuccess ? config.getStrengthSuccessCommands() : config.getTotallyFailCommands(), player);
            }
        }
    }

    static class FinalResult {
        int finalMoney;
        int finalPoints;
        Map<IModSupport.IvsEvsStats, Integer> finalIvsMap;
        Map<Integer, ItemStack> finalItems;
        Map<IModSupport.IvsEvsStats, Pair<Integer, Integer>> soulsMap;

        private FinalResult(int finalMoney, int finalPoints,
                    Map<IModSupport.IvsEvsStats, Integer> finalIvsMap,
                    Map<Integer, ItemStack> finalItems,
                    Map<IModSupport.IvsEvsStats, Pair<Integer, Integer>> soulsMap) {
            this.finalMoney = finalMoney;
            this.finalPoints = finalPoints;
            this.finalIvsMap = finalIvsMap;
            this.finalItems = finalItems;
            this.soulsMap = soulsMap;
        }

        static FinalResult get(Inventory inv, Pokemon pokemon) {
            return get(inv, pokemon, false);
        }

        static FinalResult get(Inventory inv, Pokemon pokemon, boolean onlyMoney) {
            IModSupport mod = PixelmonStrengthen.getInstance().getModSupport();
            Config config = PixelmonStrengthen.getInstance().getPluginConfig();
            Config.PokemonSettings settings = config.getPokemonSettings(pokemon);
            boolean isRemoveAllSouls = config.isRemoveAllSouls();
            int souls = 0;
            Map<IModSupport.IvsEvsStats, Integer> finalIvsMap = new HashMap<>();
            Map<Integer, ItemStack> finalItems = new HashMap<>();
            Map<IModSupport.IvsEvsStats, Pair<Integer, Integer>> soulsMap = new HashMap<>();
            // 计算六格增加的个体值
            for (int i = 0; i < 6; i++) {
                ItemStack item = inv.getItem(28 + i);
                // 检查灵魂可用性
                Optional<Integer> soulIvs = PixelmonStrengthen.getInstance().checkSoul(item, pokemon);
                if (!soulIvs.isPresent()) continue;
                int totalAmount = item.getAmount();
                int singleAmount = 0;
                souls += totalAmount;
                // 计算概率部分，只更新要花费的金钱时无需执行
                if (onlyMoney) continue;
                IModSupport.IvsEvsStats type = IModSupport.IvsEvsStats.get(i);
                int ivs = mod.getIvs(pokemon, type);
                boolean success = false;
                // 单独计算概率
                if (settings.isSingleRate) {
                    for (int j = 0; j < item.getAmount(); j++) {
                        if (Util.isRateAccess(settings)) continue;
                        success = true;
                        ivs += soulIvs.get();
                        singleAmount++;
                        if (!isRemoveAllSouls) {
                            item.setAmount(item.getAmount() - 1);
                        }
                        if (ivs >= 31) {
                            ivs = 31;
                            break;
                        }

                    }
                }
                // 统一计算概率
                else if (Util.isRateAccess(settings)) {
                    success = true;
                    for (int j = 0; j < item.getAmount(); j++) {
                        ivs += soulIvs.get();
                        singleAmount++;
                        if (!isRemoveAllSouls) {
                            item.setAmount(item.getAmount() - 1);
                        }
                        if (ivs >= 31) {
                            ivs = 31;
                            break;
                        }
                    }
                }
                if (success) finalIvsMap.put(type, ivs);
                soulsMap.put(type, Pair.of(singleAmount, totalAmount));
                finalItems.put(28 + i, isRemoveAllSouls || item.getAmount() <= 0 ? null : item);
            }
            return new FinalResult(
                    (settings.isSingleMoney ? souls : 1) * settings.useMoney,
                    (settings.isSingleMoney ? souls : 1) * settings.usePoints,
                    finalIvsMap, finalItems, soulsMap);
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        List<ItemStack> items = new ArrayList<>();
        for (int i = 28; i < 34; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null) {
                items.add(item);
            }
        }
        Util.giveItemsToPlayer(player, items);
    }
}

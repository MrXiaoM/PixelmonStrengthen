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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import top.mrxiaom.pixelmonstrengthen.*;
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
    BukkitTask updateTask;
    Map<Integer, Pair<Integer, List<String>>> visualSelection = new HashMap<>();
    Map<Integer, Map<String, Integer>> visualSouls = new HashMap<>();
    public GuiStrength(Player player, int slot) {
        this.player = player;
        this.slot = slot;
        for (int i = 0; i < 6; i++) visualSouls.put(i, new HashMap<>());
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
        Config config = PixelmonStrengthen.getInstance().getPluginConfig();
        if (!config.isAllowEgg() && pokemon.isEgg()) {
            player.sendMessage(Lang.get("errors.disallow-egg", true));
            return null;
        }
        this.pokemon = pokemon;
        inventory = Bukkit.createInventory(null, 54, Lang.get("gui.strength.title"));
        ItemStack frame = Lang.getItem("gui.strength.items.frame", player);
        ItemStack frame1 = Lang.getItem("gui.strength.items.frame-1", player);
        ItemStackUtil.setItemInLine(inventory, frame1, 2);
        ItemStackUtil.setItemInLine(inventory, frame1, 5);
        ItemStackUtil.setItemIn(inventory, frame1, 34);
        ItemStackUtil.setItemAroundInv(inventory, frame);
        inventory.setItem(10, Lang.getItem("gui.strength.items.pokemon", player, Lang.getPokemonLoreReplaceList(pokemon)));

        inventory.setItem(16, Lang.getItem("gui.strength.items.tips", player));
        updateStartButton();

        inventory.setItem(19, Lang.getItem("gui.strength.items.frame-ivs-hp", player));
        inventory.setItem(20, Lang.getItem("gui.strength.items.frame-ivs-attack", player));
        inventory.setItem(21, Lang.getItem("gui.strength.items.frame-ivs-defence", player));
        inventory.setItem(22, Lang.getItem("gui.strength.items.frame-ivs-specialattack", player));
        inventory.setItem(23, Lang.getItem("gui.strength.items.frame-ivs-specialdefence", player));
        inventory.setItem(24, Lang.getItem("gui.strength.items.frame-ivs-speed", player));
        if (config.isUseVisualMode()) {
            int offset = config.isUseClassicMode() ? 9 : 0;

        }
        int updatePeriod = config.getUpdatePeriod();
        updateTask = Bukkit.getScheduler().runTaskTimer(PixelmonStrengthen.getInstance(), this::updateStartButton, updatePeriod, updatePeriod);
        return inventory;
    }

    private void updateVisualButtons() {
        Config config = PixelmonStrengthen.getInstance().getPluginConfig();
        int offset = 28 + (config.isUseClassicMode() ? 9 : 0);
        for(int i = 0; i < 6; i++) {
            inventory.setItem(offset + i, getVisualButton(i));
        }
    }

    private ItemStack getVisualButton(int index) {
        Map<String, Integer> souls = visualSouls.getOrDefault(index, new HashMap<>());
        Pair<Integer, List<String>> selection = visualSelection.getOrDefault(index, Pair.of(0, new ArrayList<>()));
        String selected = selection.getValue().size() > 0 ? selection.getValue().get(Math.max(0, selection.getKey()) % (selection.getValue().size() - 1)) : "";
        Optional<PlayerData.Data> result = PixelmonStrengthen.getInstance().getPlayerData().get(player.getName());
        int amount = 0;
        if (result.isPresent()){
            PlayerData.Data data = result.get();
            amount = data.getStorage().getOrDefault(selected, 0);
        }
        amount -= souls.getOrDefault(selected, 0);
        int totalAmount = 0;
        for(int i : souls.values()) totalAmount += i;
        ItemStack item = Lang.getItem("gui.strength.items.button-visual-soul", player, Lists.newArrayList(
                Pair.of("%put_total_amount%", String.valueOf(totalAmount)),
                Pair.of("%selected%", selected),
                Pair.of("amount", String.valueOf(amount))
        ));
        ItemMeta im = ItemStackUtil.getItemMeta(item);
        List<String> lore = new ArrayList<>();
        for (String s : im.getLore()) {
            if (!s.contains("%put_name%")) {
                lore.add(s);
                continue;
            }
            for (String key : souls.keySet()) {
                lore.add(s.replace("%put_name%", key).replace("%put_amount%", String.valueOf(souls.get(key))));
            }
        }
        im.setLore(lore);
        item.setItemMeta(im);
        return item;
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
        Config config = PixelmonStrengthen.getInstance().getPluginConfig();
        if (!config.isAllowEgg() && pokemon.isEgg()) {
            player.sendMessage(Lang.get("errors.disallow-egg", true));
            player.closeInventory();
            return;
        }
        if(!config.isUseClassicMode()) event.setCancelled(true);
        int slot = event.getRawSlot();
        // 设置可点击背包或者放置灵魂的格子
        if (config.isUseClassicMode() && (slot < 28 || (slot > 33 && slot < 54))) {
            event.setCancelled(true);
        }
        // 禁止 Shift 点击背包
        if (slot >= 54 && event.isShiftClick()){
            event.setCancelled(true);
        }
        // 检查放置灵魂的格子
        if (config.isUseClassicMode() && slot >= 28 && slot <= 33) {
            ItemStack cursor = event.getCursor();
            if (cursor == null || cursor.getType().equals(Material.AIR)) return;
            if (!PixelmonStrengthen.getInstance().checkSoul(cursor, pokemon).isPresent()) {
                player.sendMessage(Lang.get("errors.invalid-soul", true));
                event.setCancelled(true);
            }
            return;
        }
        if (config.isUseVisualMode()) {
            int offset = 28 + (config.isUseClassicMode() ? 9 : 0);
            // 点击虚拟灵魂碎片操作按钮
            if (slot >= offset && slot < offset + 6) {
                int index = slot - offset;
                Pair<Integer, List<String>> selection = visualSelection.get(index);
                if (event.isShiftClick()) {
                    if (event.isLeftClick() && !event.isRightClick()) {
                        // 切换选择的灵魂碎片
                        selection.setKey((selection.getKey() + 1) % (selection.getValue().size() - 1));
                        visualSelection.put(index, selection);
                        inventory.setItem(slot, getVisualButton(index));
                        return;
                    }
                }
                else {
                    if (selection.getValue().isEmpty()) return;
                    String selected = selection.getValue().get(selection.getKey() % (selection.getValue().size() - 1));
                    int availableAmount = 0;
                    Optional<PlayerData.Data> result = PixelmonStrengthen.getInstance().getPlayerData().get(player.getName());
                    if (result.isPresent()) {
                        PlayerData.Data data = result.get();
                        availableAmount = data.getStorage().getOrDefault(selected, 0);
                    }
                    Map<String, Integer> souls = visualSouls.get(index);
                    int amount = souls.getOrDefault(selected, 0);
                    if (event.isLeftClick() && !event.isRightClick()) {
                        // 增加
                        if (amount >= availableAmount) return;
                        amount++;
                    }
                    if (event.isRightClick() && !event.isLeftClick()) {
                        // 减少
                        if (amount <= 0) return;
                        amount--;
                    }
                    if (amount > 0) souls.put(selected, amount);
                    else souls.remove(selected);
                    visualSouls.put(index, souls);
                    inventory.setItem(slot, getVisualButton(index));
                }
                return;
            }
        }
        // 开始强化
        if (slot == 25) {
            int souls = 0;
            for (int i = 0; i < 6; i++) {
                ItemStack item = event.getInventory().getItem(28 + i);
                if (PixelmonStrengthen.getInstance().checkSoul(item, pokemon).isPresent()) {
                    souls += item.getAmount();
                }
            }
            if(souls <= 0) {
                player.sendMessage(Lang.get("errors.no-souls", true));
                return;
            }
            OtherPlugin api = PixelmonStrengthen.getInstance().getOtherPlugin();
            Map<IModSupport.IvsEvsStats, Integer> oldIvs = mod.getIvs(pokemon);
            // 计算结果
            FinalResult result = FinalResult.get(event.getInventory(), pokemon);
            // 算账
            if (result.finalMoney > 0 && !api.hasMoney(player, result.finalMoney)) {
                player.sendMessage(Lang.get("errors.no-money", true));
                return;
            }
            if (result.finalPoints > 0 && !api.hasPoints(player, result.finalPoints)) {
                player.sendMessage(Lang.get("errors.no-points", true));
                return;
            }
            // 扣钱
            if (result.finalMoney > 0) {
                api.takeMoney(player, result.finalMoney);
            }
            if (result.finalPoints > 0) {
                api.takePoints(player, result.finalPoints);
            }
            for (int i : result.finalItems.keySet()) {
                ItemStack item = event.getInventory().getItem(i);
                if (item != null){
                    item.setAmount(result.finalItems.get(i));
                    if(item.getAmount() > 0)
                    event.getInventory().setItem(i, item);
                    else event.getInventory().setItem(i, null);
                }
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
                    Pair.of("%pokemon_display%", pokemon.getNickname() != null ? pokemon.getNickname() : Lang.getPokemonTranslateName(pokemon)),
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
                Pair<Integer, Integer> pair = result.soulsMap.getOrDefault(stat, Pair.of(0, 0));
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
            List<String> commands = new ArrayList<>();
            for (String s : isSuccess ? config.getStrengthSuccessCommands() : config.getTotallyFailCommands()) {
                for (Pair<String, String> pair : replaceList) {
                    s = s.replace(pair.getKey(), pair.getValue());
                }
                commands.add(s);
            }
            if (isExecuteBeforeMessage) {
                Util.runCommands(commands, player);
            }
            player.sendMessage(Lang.getListAsString(isSuccess ? "strength.success" : "strength.fail", true, replaceList));
            if (!isExecuteBeforeMessage) {
                Util.runCommands(commands, player);
            }
        }
    }

    static class FinalResult {
        int finalMoney;
        int finalPoints;
        Map<IModSupport.IvsEvsStats, Integer> finalIvsMap;
        Map<Integer, Integer> finalItems;
        Map<IModSupport.IvsEvsStats, Pair<Integer, Integer>> soulsMap;

        private FinalResult(int finalMoney, int finalPoints,
                    Map<IModSupport.IvsEvsStats, Integer> finalIvsMap,
                    Map<Integer, Integer> finalItems,
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
            if (inv == null || pokemon == null) return null;
            IModSupport mod = PixelmonStrengthen.getInstance().getModSupport();
            Config config = PixelmonStrengthen.getInstance().getPluginConfig();
            Config.PokemonSettings settings = config.getPokemonSettings(pokemon);
            boolean isRemoveAllSouls = config.isRemoveAllSouls();
            int souls = 0;
            Map<IModSupport.IvsEvsStats, Integer> oldIvsMap = mod.getIvs(pokemon);
            Map<IModSupport.IvsEvsStats, Integer> finalIvsMap = new HashMap<>();
            Map<Integer, Integer> finalItems = new HashMap<>();
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
                int amount = totalAmount;
                IModSupport.IvsEvsStats type = IModSupport.IvsEvsStats.get(i);
                int ivs = mod.getIvs(pokemon, type);
                boolean success = false;
                int totalIvs = getTotalIvs(oldIvsMap, finalIvsMap);
                int totalV = getTotalV(oldIvsMap, finalIvsMap);
                // 检查是否可强化该类型
                // 检查是否超过个体限制
                if (!settings.bannedStrength.contains(type) && totalIvs < settings.maxIvs && totalV < settings.maxV &&  ivs < 31) {
                    // 单独计算概率
                    if (settings.isSingleRate) {
                        for (int j = 0; j < item.getAmount(); j++) {
                            if (Util.isRateAccess(settings)) continue;
                            success = true;
                            ivs += soulIvs.get();
                            singleAmount++;
                            if (!isRemoveAllSouls) {
                                amount--;
                            }
                            if (ivs >= 31) {
                                ivs = 31;
                                break;
                            }
                            // 检查是否超过个体限制
                            totalIvs = getTotalIvs(oldIvsMap, finalIvsMap, type, ivs);
                            totalV = getTotalV(oldIvsMap, finalIvsMap, type, ivs);
                            if (totalIvs >= settings.maxIvs || totalV >= settings.maxV) break;
                        }
                    }
                    // 统一计算概率
                    else if (Util.isRateAccess(settings)) {
                        success = true;
                        for (int j = 0; j < item.getAmount(); j++) {
                            ivs += soulIvs.get();
                            singleAmount++;
                            if (!isRemoveAllSouls) {
                                amount--;
                            }
                            if (ivs >= 31) {
                                ivs = 31;
                                break;
                            }
                            // 检查是否超过个体限制
                            totalIvs = getTotalIvs(oldIvsMap, finalIvsMap, type, ivs);
                            totalV = getTotalV(oldIvsMap, finalIvsMap, type, ivs);
                            if (totalIvs >= settings.maxIvs || totalV >= settings.maxV) break;
                        }
                    }
                }
                if (success) finalIvsMap.put(type, ivs);
                soulsMap.put(type, Pair.of(singleAmount, totalAmount));
                finalItems.put(28 + i, isRemoveAllSouls ? 0 : Math.max(0, amount));
            }
            return new FinalResult(
                    (settings.isSingleMoney ? souls : 1) * settings.useMoney,
                    (settings.isSingleMoney ? souls : 1) * settings.usePoints,
                    finalIvsMap, finalItems, soulsMap);
        }

        static int getTotalIvs(Map<IModSupport.IvsEvsStats, Integer> oldIvsMap, Map<IModSupport.IvsEvsStats, Integer> finalIvsMap) {
            return getTotalIvs(oldIvsMap, finalIvsMap, null, 0);
        }

        static int getTotalIvs(Map<IModSupport.IvsEvsStats, Integer> oldIvsMap, Map<IModSupport.IvsEvsStats, Integer> finalIvsMap, IModSupport.IvsEvsStats extra, int extraIvs) {
            int ivs = 0;
            for(IModSupport.IvsEvsStats stat : oldIvsMap.keySet()) {
                if (stat.equals(extra)){
                    ivs += extraIvs;
                    continue;
                }
                ivs += finalIvsMap.getOrDefault(stat, oldIvsMap.get(stat));
            }
            return ivs;
        }

        static int getTotalV(Map<IModSupport.IvsEvsStats, Integer> oldIvsMap, Map<IModSupport.IvsEvsStats, Integer> finalIvsMap) {
            return getTotalV(oldIvsMap, finalIvsMap, null, 0);
        }

        static int getTotalV(Map<IModSupport.IvsEvsStats, Integer> oldIvsMap, Map<IModSupport.IvsEvsStats, Integer> finalIvsMap, IModSupport.IvsEvsStats extra, int extraIvs) {
            int v = 0;
            for(IModSupport.IvsEvsStats stat : oldIvsMap.keySet()) {
                if (stat.equals(extra) && extraIvs == 31) {
                    v++;
                    continue;
                }
                if(finalIvsMap.getOrDefault(stat, oldIvsMap.get(stat)) == 31) v++;
            }
            return v;
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (updateTask != null){
            updateTask.cancel();
        }
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

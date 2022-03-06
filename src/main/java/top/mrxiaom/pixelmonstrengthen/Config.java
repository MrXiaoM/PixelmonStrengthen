package top.mrxiaom.pixelmonstrengthen;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.mrxiaom.pixelmonstrengthen.utils.pixelmonmod.IModSupport;
import top.mrxiaom.pixelmonstrengthen.utils.ItemStackUtil;
import top.mrxiaom.pixelmonstrengthen.utils.Pair;
import top.mrxiaom.pixelmonstrengthen.utils.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Config {
    PixelmonStrengthen plugin;
    FileConfiguration config;

    public static class PokemonSettings {
        public double rate;
        public boolean isSingleRate;
        public int ivs;
        public int maxIvs;
        public int maxV;
        public int amount;
        public List<IModSupport.IvsEvsStats> bannedStrength;
        public int useMoney;
        public boolean isSingleMoney;
        public int usePoints;
        public boolean isSinglePoints;
        public String override;
        public PokemonSettings(double rate, boolean isSingleRate, int ivs, int maxIvs, int maxV, int amount, List<IModSupport.IvsEvsStats> bannedStrength, int useMoney, boolean isSingleMoney, int usePoints, boolean isSinglePoints, String override) {
            this.rate = rate;
            this.isSingleRate = isSingleRate;
            this.ivs = ivs;
            this.maxIvs = maxIvs;
            this.maxV = maxV;
            this.amount = amount;
            this.bannedStrength = bannedStrength;
            this.useMoney = useMoney;
            this.isSingleMoney = isSingleMoney;
            this.usePoints = usePoints;
            this.isSinglePoints = isSinglePoints;
            this.override = override;
        }
    }

    enum BanMode {
        BLACKLIST, WHITELIST
    }
    boolean isUseVisualMode;
    boolean isUseClassicMode;
    boolean isAllowVisualDecompose;
    boolean isAllowClassicDecompose;
    boolean isRemoveAllSouls;
    boolean isAllowEgg;
    boolean isExecuteBeforeMessage;
    int updatePeriod;
    BanMode banMode;
    List<String> strengthSuccessCommands;
    List<String> totallyFailCommands;

    public Config(PixelmonStrengthen plugin) {
        this.plugin = plugin;
    }

    public PokemonSettings getPokemonSettings(Pokemon pokemon) {
        return getPokemonSettings(plugin.getModSupport().getPokemonBaseName(pokemon));
    }

    public PokemonSettings getPokemonSettings(String baseName) {
        String root = "pokemons." + baseName + ".";
        double rate = config.getDouble(root + "rate", config.getDouble("pokemons.default.rate", 100.0d));
        boolean isSingleRate = config.getBoolean(root + "single-rate", config.getBoolean("pokemons.default.single-rate", false));
        int ivs = config.getInt(root + "ivs", config.getInt("pokemons.default.ivs", 1));
        int maxIvs = config.getInt(root + "max-ivs", config.getInt("pokemons.default.max-ivs", 186));
        int maxV = config.getInt(root + "max-v", config.getInt("pokemons.default.max-v", 3));
        int amount = config.getInt(root + "amount", config.getInt("pokemons.default.amount", 2));
        int useMoney = config.getInt(root + "use-money", config.getInt("pokemons.default.use-money", 100));
        boolean isSingleMoney = config.getBoolean(root + "single-money", config.getBoolean("pokemons.default.single-money", false));
        int usePoints = config.getInt(root + "use-points", config.getInt("pokemons.default.use-points", 0));
        boolean isSinglePoints = config.getBoolean(root + "single-points", config.getBoolean("pokemons.default.single-points", false));
        List<IModSupport.IvsEvsStats> bannedStrength = new ArrayList<>();
        List<String> banned = config.contains(root + "banned-strength") && config.isList(root + "banned-strength") ? config.getStringList(root + "banned-strength")
                : (config.contains("pokemons.default.banned-strength") && config.isList("pokemons.default.banned-strength") ? config.getStringList("pokemons.default.banned-strength") : new ArrayList<>());
        for (String s : banned) {
            Optional<IModSupport.IvsEvsStats> stats = Util.valueOf(IModSupport.IvsEvsStats.class, s);
            stats.ifPresent(bannedStrength::add);
        }
        String override = config.getString(root + "override", baseName);
        return new PokemonSettings(rate, isSingleRate, ivs, maxIvs, maxV, amount, bannedStrength, useMoney, isSingleMoney, usePoints, isSinglePoints, override);
    }

    public Config reloadConfig() {
        config = plugin.getConfig();
        // 处理旧的配置文件
        if (config.contains("soul-item-id")) {
            File oldConfig = new File(plugin.getDataFolder(), "config.yml");
            File backupConfig = new File(plugin.getDataFolder(), "config_backup_1.x.yml");
            plugin.getLogger().warning("发现旧的配置文件格式，正在备份");
            try {
                if (oldConfig.renameTo(backupConfig)) return reloadConfig();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            plugin.getLogger().warning("无法备份旧的配置文件，请手动移除配置文件再重载插件，否则插件可能无法正常运行");
            return this;
        }
        Lang.loadLanguage(plugin);
        isUseVisualMode = config.getBoolean("visual-mode", false);
        isUseClassicMode = config.getBoolean("classic-mode", true);
        isAllowVisualDecompose = config.getBoolean("allow-visual-decompose", true);
        isAllowClassicDecompose = config.getBoolean("allow-classic-decompose", true);
        isAllowEgg = config.getBoolean("is-allow-egg", false);
        isRemoveAllSouls = config.getBoolean("remove-all-souls", false);
        isExecuteBeforeMessage = config.getBoolean("execute-before-message", true);
        updatePeriod = config.getInt("update-period", 5);
        banMode = Util.valueOf(BanMode.class, config.getString("ban-mode", "BLACKLIST")).orElse(BanMode.BLACKLIST);
        strengthSuccessCommands = config.getStringList("strength-success-commands");
        totallyFailCommands = config.getStringList("totally-fail-commands");
        return this;
    }

    public ItemStack getPokemonSoul(Player player, Pokemon pokemon) {
        PokemonSettings settings = getPokemonSettings(pokemon);
        return getPokemonSoul(player, settings.override, settings.ivs, settings.amount);
    }

    public ItemStack getPokemonSoul(Player player, String baseName, int perIvs, int amount) {
        return getPokemonSoul(player, baseName, Lang.getPokemonTranslateName(baseName), perIvs, amount);
    }

    public ItemStack getPokemonSoul(Player player, String baseName, String name, int perIvs, int amount) {
        return getPokemonSoul(player, baseName, name, perIvs, amount, false);
    }

    public ItemStack getPokemonSoul(Player player, String baseName, String name, int perIvs, int amount, boolean visual) {
        List<Pair<String, String>> replaceList = Lists.newArrayList(
                Pair.of("%pokemon_name%", name),
                Pair.of("%pokemon_basename%", baseName),
                Pair.of("%pokemon_soul_per_ivs%", String.valueOf(perIvs))
        );
        if (visual) {
            replaceList.addAll(Lists.newArrayList(
                    Pair.of("%amount%", String.valueOf(amount))
            ));
        }
        String root = (visual ? "visual-" : "") + "soul.";
        Object material = config.get(root + "material");
        int data = config.getInt(root + "data", 0);
        String display = config.getString(root + "name");
        for (Pair<String, String> pair : replaceList) {
            display = display.replace(pair.getKey(), pair.getValue());
        }
        display = Util.handlePlaceholders(player, display);
        List<String> lore = config.getStringList(root + "lore");
        ItemStack item;
        if (material instanceof Integer) {
            int id = (Integer) material;
            item = ItemStackUtil.build(player, id, data, display, lore, replaceList);
        } else {
            Material m = Material.STONE;
            try {
                m = Material.valueOf(material.toString().toUpperCase());
            } catch (Throwable ignored) {
                PixelmonStrengthen.getInstance().getLogger().warning("物品ID " + material.toString() + " 无效");
            }
            item = ItemStackUtil.build(player, m, data, 1, display, lore, replaceList);
        }
        item.setAmount(Math.max(1, Math.min(amount, 64)));
        for (String s : config.getStringList(root + "enchants")) {
            if (s.contains(":")) {
                Optional<Enchantment> ench = ItemStackUtil.getEnchantment(s.substring(0, s.indexOf(":")));
                if (ench.isPresent()) {
                    int level = Util.parseInt(s.substring(s.indexOf(":") + 1), 0);
                    if (level < 1) continue;
                    item.addUnsafeEnchantment(ench.get(), level);
                }
            }
        }
        ItemMeta meta = ItemStackUtil.getItemMeta(item);
        for (String s : config.getStringList(root + "flags")) {
            Optional<ItemFlag> flag = Util.valueOf(ItemFlag.class, s);
            flag.ifPresent(meta::addItemFlags);
        }
        item.setItemMeta(meta);
        if (!visual) {
            item = ItemStackUtil.writeNBT(item, "PokemonSoulType", baseName.toLowerCase());
            item = ItemStackUtil.writeNBT(item, "PokemonSoulIvs", perIvs);
        }
        return item;
    }

    public boolean checkPermission(Player player, Pokemon pokemon) {
        String baseName = plugin.getModSupport().getPokemonBaseName(pokemon);
        if (banMode.equals(BanMode.WHITELIST)) {
            return player.hasPermission("pixelmonstrengthen.whitelist." + baseName);
        } else {
            return !player.hasPermission("pixelmonstrengthen.blacklist." + baseName);
        }
    }

    public boolean isUseVisualMode() {
        return isUseVisualMode;
    }

    public boolean isUseClassicMode() {
        return isUseClassicMode;
    }

    public boolean isAllowVisualDecompose() {
        return isAllowVisualDecompose;
    }

    public boolean isAllowClassicDecompose() {
        return isAllowClassicDecompose;
    }

    public boolean isRemoveAllSouls() {
        return isRemoveAllSouls;
    }

    public boolean isAllowEgg() {
        return isAllowEgg;
    }

    public boolean isExecuteBeforeMessage() {
        return isExecuteBeforeMessage;
    }

    public int getUpdatePeriod() {
        return updatePeriod;
    }

    public BanMode getBanMode() {
        return banMode;
    }

    public List<String> getStrengthSuccessCommands() {
        return strengthSuccessCommands;
    }

    public List<String> getTotallyFailCommands() {
        return totallyFailCommands;
    }

}

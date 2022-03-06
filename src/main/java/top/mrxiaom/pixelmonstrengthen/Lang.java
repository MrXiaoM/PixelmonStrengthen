package top.mrxiaom.pixelmonstrengthen;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.pixelmonstrengthen.utils.pixelmonmod.IModSupport;
import top.mrxiaom.pixelmonstrengthen.utils.ItemStackUtil;
import top.mrxiaom.pixelmonstrengthen.utils.Pair;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lang {
    private static YamlConfiguration config = new YamlConfiguration();

    public static void loadLanguage(PixelmonStrengthen plugin) {
        String langFileName = plugin.getConfig().getString("lang-file", "zh-cn.yml");
        File langFile = new File(plugin.getDataFolder(), langFileName);
        if (!langFile.exists() && plugin.getResource(langFileName) != null) {
            plugin.saveResource(langFileName, false);
        }
        if (langFile.exists()) {
            Lang.loadFromFile(langFile);
        } else {
            plugin.getLogger().warning("找不到指定的语言文件 " + langFileName);
        }

        String pixelmonLangFileName = plugin.getConfig().getString("pixelmon-lang", "zh_CN.lang");
        File pixelmonLangFile = new File(plugin.getDataFolder(), pixelmonLangFileName);
        if (!pixelmonLangFile.exists() && plugin.getResource(pixelmonLangFileName) != null) {
            plugin.saveResource(pixelmonLangFileName, false);
        }
        if (pixelmonLangFile.exists()) {
            Lang.loadPixelmonNameFromFile(pixelmonLangFile);
        } else {
            plugin.getLogger().warning("找不到指定的宝可梦语言文件 " + pixelmonLangFileName);
        }
    }

    public static void loadFromFile(File file) {
        try {
            config = YamlConfiguration.loadConfiguration(file);
        } catch (Throwable t) {
            PixelmonStrengthen.getInstance().getLogger().warning("语言文件加载失败");
            t.printStackTrace();
        }
    }

    private static final Map<String, String> pixelmonLang = new HashMap<>();

    public static void loadPixelmonNameFromFile(File file) {
        pixelmonLang.clear();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String s : lines) {
                if (s.startsWith("#") || !s.contains("=")) continue;
                pixelmonLang.put(s.substring(0, s.indexOf("=")), s.substring(s.indexOf("=") + 1));
            }
        } catch (Throwable t) {
            PixelmonStrengthen.getInstance().getLogger().warning("读取宝可梦语言文件失败");
            t.printStackTrace();
        }
    }

    public static String getPokemonTranslateName(Pokemon pokemon) {
        return getPokemonTranslateName(PixelmonStrengthen.getInstance().getModSupport().getPokemonBaseName(pokemon));
    }

    public static String getPokemonTranslateName(String baseName) {
        return pixelmonLang.getOrDefault("pixelmon." + baseName.toLowerCase() + ".name", baseName);
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    public static String get(String key) {
        return get(key, false);
    }

    public static String get(String key, boolean hasPrefix) {
        return ChatColor.translateAlternateColorCodes('&', (hasPrefix ? config.getString("prefix", "[404:prefix]") : "") + config.getString(key, "[494:" + key + "]"));
    }

    public static String getListAsString(String key, boolean hasPrefix) {
        return getListAsString(key, hasPrefix, Lists.newArrayList());
    }

    public static String getListAsString(String key, boolean hasPrefix, List<Pair<String, String>> replaceList) {
        if (config.contains(key) && config.isList(key)) {
            String prefix = hasPrefix ? config.getString("prefix", "[404:prefix]") : "";
            StringBuilder result = new StringBuilder();
            for (String s : config.getStringList(key)) {
                for (Pair<String, String> pair : replaceList) {
                    s = s.replace(pair.getKey(), pair.getValue());
                }
                result.append(prefix).append(s).append("\n");
            }
            if (result.length() > 0) result.delete(result.length() - 1, result.length());
            return ChatColor.translateAlternateColorCodes('&', result.toString());
        }
        return "[404:" + key + "]";
    }

    public static List<String> getList(String key, boolean hasPrefix) {
        return getList(key, hasPrefix, Lists.newArrayList());
    }
    public static List<String> getList(String key, boolean hasPrefix, List<Pair<String, String>> replaceList) {
        if (config.contains(key) && config.isList(key)) {
            String prefix = hasPrefix ? config.getString("prefix", "[404:prefix]") : "";
            List<String> result = new ArrayList<>();
            for (String s : config.getStringList(key)) {
                for (Pair<String, String> pair : replaceList) {
                    s = s.replace(pair.getKey(), pair.getValue());
                }
                result.add(ChatColor.translateAlternateColorCodes('&', prefix + s));
            }
            return result;
        }
        return Lists.newArrayList("[404:" + key + "]");
    }

    public static String pokemonGet(String key) {
        return pixelmonLang.getOrDefault(key, key);
    }

    public static ItemStack getItem(String root, Player player) {
        return getItem(root, player, new ArrayList<>());
    }

    public static ItemStack getItem(String root, Player player, List<Pair<String, String>> replaceList) {
        Object material = config.get(root + ".material");
        int data = config.getInt(root + ".data", 0);
        String display = config.getString(root + ".name");
        List<String> lore = config.getStringList(root + ".lore");
        if (material instanceof Integer) {
            int id = (Integer) material;
            return ItemStackUtil.build(player, id, data, display, lore, replaceList);
        } else {
            Material m = Material.STONE;
            try {
                m = Material.valueOf(material.toString().toUpperCase());
            } catch (Throwable ignored) {
                PixelmonStrengthen.getInstance().getLogger().warning("物品ID " + material.toString() + " 无效");
            }
            return ItemStackUtil.build(player, m, data, 1, display, lore, replaceList);
        }
    }

    public static List<Pair<String, String>> getPokemonLoreReplaceList(Pokemon pokemon) {
        IModSupport mod = PixelmonStrengthen.getInstance().getModSupport();
        return Lists.newArrayList(
                Pair.of("%v%", String.valueOf(mod.getV(pokemon))),
                Pair.of("%pokemon_display%", pokemon.getNickname() != null ? pokemon.getNickname() : Lang.getPokemonTranslateName(pokemon)),
                Pair.of("%pokemon_name%", Lang.getPokemonTranslateName(pokemon)),
                Pair.of("%ivs_hp%", String.valueOf(mod.getIvs(pokemon, IModSupport.IvsEvsStats.HP))),
                Pair.of("%ivs_attack%", String.valueOf(mod.getIvs(pokemon, IModSupport.IvsEvsStats.Attack))),
                Pair.of("%ivs_defence%", String.valueOf(mod.getIvs(pokemon, IModSupport.IvsEvsStats.Defence))),
                Pair.of("%ivs_specialattack%", String.valueOf(mod.getIvs(pokemon, IModSupport.IvsEvsStats.SpecialAttack))),
                Pair.of("%ivs_specialdefence%", String.valueOf(mod.getIvs(pokemon, IModSupport.IvsEvsStats.SpecialDefence))),
                Pair.of("%ivs_speed%", String.valueOf(mod.getIvs(pokemon, IModSupport.IvsEvsStats.Speed))));
    }

    public static boolean notPlayer(CommandSender sender) {
        sender.sendMessage(Lang.get("errors.not-player", true));
        return true;
    }

    public static boolean noPerm(CommandSender sender) {
        sender.sendMessage(Lang.get("errors.no-permission", true));
        return true;
    }
}

package top.mrxiaom.pixelmonstrengthen.utils;

import com.google.common.collect.Lists;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.pixelmonstrengthen.Config;
import top.mrxiaom.pixelmonstrengthen.Lang;

import java.util.*;

public class Util {
    public static boolean hasPAPI = false;

    public static int parseInt(String s) {
        return parseInt(s, null);
    }

    public static int parseInt(String s, Integer nullValue) {
        try {
            return Integer.parseInt(s);
        } catch (Throwable ignored) {
            return nullValue;
        }
    }

    public static float parseFloat(String s) {
        return parseFloat(s, null);
    }

    public static float parseFloat(String s, Float nullValue) {
        try {
            return Float.parseFloat(s);
        } catch (Throwable ignored) {
            return nullValue;
        }
    }

    public static <T extends Enum<T>> Optional<T> valueOf(Class<T> enumType, String name) {
        for (T t : enumType.getEnumConstants()) {
            if (t.name().equalsIgnoreCase(name))
                return Optional.of(t);
        }
        return Optional.empty();
    }

    public static List<String> handlePlaceholders(Player player, List<String> list) {
        List<String> result = new ArrayList<>();
        for (String s : list) result.add(handlePlaceholders(player, s));
        return result;
    }

    public static String handlePlaceholders(Player player, String s) {
        if (!hasPAPI) {
            String result = ChatColor.translateAlternateColorCodes('&', s);
            if (player == null) return result;
            return result.replace("%player_name%", player.getName());
        }
        return PlaceholderAPI.setPlaceholders(player, s);
    }

    public static String getNMSVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    public static void giveItemsToPlayer(Player player, ItemStack... items) {
        giveItemsToPlayer(player, Lists.newArrayList(items));
    }

    public static void giveItemsToPlayer(Player player, Collection<ItemStack> items) {
        List<ItemStack> overflowItems = new ArrayList<>();
        for (ItemStack item : items) {
            if (item != null) {
                Collection<ItemStack> _items = player.getInventory().addItem(item).values();
                if (!_items.isEmpty()) {
                    overflowItems.addAll(_items);
                }
            }
        }
        if (!overflowItems.isEmpty()) {
            player.sendMessage(Lang.get("inv-full", true));
            overflowItems.forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
        }
    }

    public static boolean isRateAccess(Config.PokemonSettings settings) {
        return new Random().nextInt(1000) / 10.0D > (100.0D - settings.rate);
    }

    public static boolean runCommands(List<String> commands, Player player) {
        boolean flag = true;
        if (player == null || commands == null || commands.isEmpty())
            return false;
        for (String cmd : handlePlaceholders(player, commands)) {
            if (cmd.startsWith("console:")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.substring(8));
                continue;
            }
            if (cmd.startsWith("message-all:")) {
                Bukkit.broadcastMessage(cmd.substring(12));
                continue;
            }
            if (player.isOnline()) {
                if (cmd.startsWith("player:")) {
                    Bukkit.dispatchCommand(player, cmd.substring(7));
                    continue;
                }
                if (cmd.startsWith("sound:")) {
                    String s = cmd.substring(6).toUpperCase();
                    String[] a = s.contains(",") ? s.split(",") : new String[]{s};
                    float volume = parseFloat(a.length > 1 ? a[1] : "1.0", 1.0F);
                    float pitch = parseFloat(a.length > 2 ? a[2] : "1.0", 1.0F);
                    Optional<Sound> sound = valueOf(Sound.class, a[0]);
                    sound.ifPresent(value -> player.playSound(player.getLocation(), value, volume, pitch));
                    continue;
                }
                if (cmd.startsWith("message:")) {
                    player.sendMessage(cmd.substring(8));
                }
            }
        }
        return flag;
    }

    @Deprecated
    public static String clearColor(String text) {
        return text
                .replace("§a", "")
                .replace("§b", "")
                .replace("§c", "")
                .replace("§d", "")
                .replace("§e", "")
                .replace("§f", "")
                .replace("§r", "")
                .replace("§l", "")
                .replace("§m", "")
                .replace("§n", "")
                .replace("§o", "")
                .replace("§k", "")
                .replace("§0", "")
                .replace("§1", "")
                .replace("§2", "")
                .replace("§3", "")
                .replace("§4", "")
                .replace("§5", "")
                .replace("§6", "")
                .replace("§7", "")
                .replace("§8", "")
                .replace("§9", "")
                .replace("§A", "")
                .replace("§B", "")
                .replace("§C", "")
                .replace("§D", "")
                .replace("§E", "")
                .replace("§F", "")
                .replace("§R", "")
                .replace("§L", "")
                .replace("§M", "")
                .replace("§N", "")
                .replace("§O", "")
                .replace("§K", "");
    }
}

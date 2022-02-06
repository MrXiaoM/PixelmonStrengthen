package top.mrxiaom.pixelmonstrengthen.utils;

import com.google.common.collect.Lists;
import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemStackUtil {

    public static ItemMeta getItemMeta(ItemStack item) {
        ItemMeta im = item.getItemMeta();
        if (im != null) return im;
        return getItemMeta(item.getType());
    }

    public static ItemMeta getItemMeta(Material material) {
        try {
            Class<?> classCraftItemFactory = Class
                    .forName("org.bukkit.craftbukkit." + Util.getNMSVersion() + ".inventory.CraftItemFactory");
            Method instance = classCraftItemFactory.getDeclaredMethod("instance");
            Method getItemMeta = classCraftItemFactory.getDeclaredMethod("getItemMeta", Material.class);
            Object craftItemFactory = instance.invoke(null);
            return (ItemMeta) getItemMeta.invoke(craftItemFactory, material);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("all")
    public static ItemStack decideItem(Material material, int id, int data, int amount) {
        if (material == null) {
            return data > 0 ? new ItemStack(id, amount, Short.parseShort(String.valueOf(data))) : new ItemStack(id, amount);
        }
        return data > 0 ? new ItemStack(material, amount, Short.parseShort(String.valueOf(data))) : new ItemStack(material, amount);
    }

    public static ItemStack build(int id, int data, String display) {
        return build(null, null, id, data, 1, display, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static ItemStack build(int id, int data, String display, String... lore) {
        return build(null, null, id, data, 1, display, Lists.newArrayList(lore), new ArrayList<>(), false);
    }

    public static ItemStack build(int id, int data, String display, List<String> lore) {
        return build(null, null, id, data, 1, display, lore, new ArrayList<>(), false);
    }

    public static ItemStack build(int id, int data, String display, List<String> lore, List<Pair<String, String>> loreReplaceList) {
        return build(null, null, id, data, 1, display, lore, loreReplaceList, false);
    }

    public static ItemStack build(int id, int data, String display, List<String> lore, List<Pair<String, String>> loreReplaceList, boolean glow) {
        return build(null, null, id, data, 1, display, lore, loreReplaceList, glow);
    }

    public static ItemStack build(int id, int data, int amount, String display) {
        return build(null, null, id, data, amount, display, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static ItemStack build(int id, int data, int amount, String display, String... lore) {
        return build(null, null, id, data, amount, display, Lists.newArrayList(lore), new ArrayList<>(), false);
    }

    public static ItemStack build(int id, int data, int amount, String display, List<String> lore) {
        return build(null, null, id, data, amount, display, lore, new ArrayList<>(), false);
    }

    public static ItemStack build(int id, int data, int amount, String display, List<String> lore, List<Pair<String, String>> loreReplaceList) {
        return build(null, null, id, data, amount, display, lore, loreReplaceList, false);
    }

    public static ItemStack build(int id, int data, int amount, String display, List<String> lore, List<Pair<String, String>> loreReplaceList, boolean glow) {
        return build(null, null, id, data, amount, display, lore, loreReplaceList, glow);
    }

    public static ItemStack build(Material material, String display) {
        return build(null, material, 0, 0, 1, display, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static ItemStack build(Material material, String display, String... lore) {
        return build(null, material, 0, 0, 1, display, Lists.newArrayList(lore), new ArrayList<>(), false);
    }

    public static ItemStack build(Material material, String display, List<String> lore) {
        return build(null, material, 0, 0, 1, display, lore, new ArrayList<>(), false);
    }

    public static ItemStack build(Material material, String display, List<String> lore, List<Pair<String, String>> loreReplaceList) {
        return build(null, material, 0, 0, 1, display, lore, loreReplaceList, false);
    }

    public static ItemStack build(Material material, String display, List<String> lore, List<Pair<String, String>> loreReplaceList, boolean glow) {
        return build(null, material, 0, 0, 1, display, lore, loreReplaceList, glow);
    }

    public static ItemStack build(Material material, int amount, String display) {
        return build(null, material, 0, 0, amount, display, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static ItemStack build(Material material, int amount, String display, String... lore) {
        return build(null, material, 0, 0, amount, display, Lists.newArrayList(lore), new ArrayList<>(), false);
    }

    public static ItemStack build(Material material, int amount, String display, List<String> lore) {
        return build(null, material, 0, 0, amount, display, lore, new ArrayList<>(), false);
    }

    public static ItemStack build(Material material, int amount, String display, List<String> lore, List<Pair<String, String>> loreReplaceList) {
        return build(null, material, 0, 0, amount, display, lore, loreReplaceList, false);
    }

    public static ItemStack build(Material material, int amount, String display, List<String> lore, List<Pair<String, String>> loreReplaceList, boolean glow) {
        return build(null, material, 0, 0, amount, display, lore, loreReplaceList, glow);
    }

    public static ItemStack build(Material material, int data, int amount, String display) {
        return build(null, material, 0, data, amount, display, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static ItemStack build(Material material, int data, int amount, String display, String... lore) {
        return build(null, material, 0, data, amount, display, Lists.newArrayList(lore), new ArrayList<>(), false);
    }

    public static ItemStack build(Material material, int data, int amount, String display, List<String> lore) {
        return build(null, material, 0, data, amount, display, lore, new ArrayList<>(), false);
    }

    public static ItemStack build(Material material, int data, int amount, String display, List<String> lore, List<Pair<String, String>> loreReplaceList) {
        return build(null, material, 0, data, amount, display, lore, loreReplaceList, false);
    }

    public static ItemStack build(Material material, int data, int amount, String display, List<String> lore, List<Pair<String, String>> loreReplaceList, boolean glow) {
        return build(null, material, 0, data, amount, display, lore, loreReplaceList, glow);
    }

    public static ItemStack build(Player player, int id, int data, String display) {
        return build(player, null, id, data, 1, display, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static ItemStack build(Player player, int id, int data, String display, String... lore) {
        return build(player, null, id, data, 1, display, Lists.newArrayList(lore), new ArrayList<>(), false);
    }

    public static ItemStack build(Player player, int id, int data, String display, List<String> lore) {
        return build(player, null, id, data, 1, display, lore, new ArrayList<>(), false);
    }

    public static ItemStack build(Player player, int id, int data, String display, List<String> lore, List<Pair<String, String>> loreReplaceList) {
        return build(player, null, id, data, 1, display, lore, loreReplaceList, false);
    }

    public static ItemStack build(Player player, int id, int data, String display, List<String> lore, List<Pair<String, String>> loreReplaceList, boolean glow) {
        return build(player, null, id, data, 1, display, lore, loreReplaceList, glow);
    }

    public static ItemStack build(Player player, int id, int data, int amount, String display) {
        return build(player, null, id, data, amount, display, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static ItemStack build(Player player, int id, int data, int amount, String display, String... lore) {
        return build(player, null, id, data, amount, display, Lists.newArrayList(lore), new ArrayList<>(), false);
    }

    public static ItemStack build(Player player, int id, int data, int amount, String display, List<String> lore) {
        return build(player, null, id, data, amount, display, lore, new ArrayList<>(), false);
    }

    public static ItemStack build(Player player, int id, int data, int amount, String display, List<String> lore, List<Pair<String, String>> loreReplaceList) {
        return build(player, null, id, data, amount, display, lore, loreReplaceList, false);
    }

    public static ItemStack build(Player player, int id, int data, int amount, String display, List<String> lore, List<Pair<String, String>> loreReplaceList, boolean glow) {
        return build(player, null, id, data, amount, display, lore, loreReplaceList, glow);
    }

    public static ItemStack build(Player player, Material material, String display) {
        return build(player, material, 0, 0, 1, display, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static ItemStack build(Player player, Material material, String display, String... lore) {
        return build(player, material, 0, 0, 1, display, Lists.newArrayList(lore), new ArrayList<>(), false);
    }

    public static ItemStack build(Player player, Material material, String display, List<String> lore) {
        return build(player, material, 0, 0, 1, display, lore, new ArrayList<>(), false);
    }

    public static ItemStack build(Player player, Material material, String display, List<String> lore, List<Pair<String, String>> loreReplaceList) {
        return build(player, material, 0, 0, 1, display, lore, loreReplaceList, false);
    }

    public static ItemStack build(Player player, Material material, String display, List<String> lore, List<Pair<String, String>> loreReplaceList, boolean glow) {
        return build(player, material, 0, 0, 1, display, lore, loreReplaceList, glow);
    }

    public static ItemStack build(Player player, Material material, int amount, String display) {
        return build(player, material, 0, 0, amount, display, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static ItemStack build(Player player, Material material, int amount, String display, String... lore) {
        return build(player, material, 0, 0, amount, display, Lists.newArrayList(lore), new ArrayList<>(), false);
    }

    public static ItemStack build(Player player, Material material, int amount, String display, List<String> lore) {
        return build(player, material, 0, 0, amount, display, lore, new ArrayList<>(), false);
    }

    public static ItemStack build(Player player, Material material, int amount, String display, List<String> lore, List<Pair<String, String>> loreReplaceList) {
        return build(player, material, 0, 0, amount, display, lore, loreReplaceList, false);
    }

    public static ItemStack build(Player player, Material material, int amount, String display, List<String> lore, List<Pair<String, String>> loreReplaceList, boolean glow) {
        return build(player, material, 0, 0, amount, display, lore, loreReplaceList, glow);
    }

    public static ItemStack build(Player player, Material material, int data, int amount, String display) {
        return build(player, material, 0, data, amount, display, new ArrayList<>(), new ArrayList<>(), false);
    }

    public static ItemStack build(Player player, Material material, int data, int amount, String display, String... lore) {
        return build(player, material, 0, data, amount, display, Lists.newArrayList(lore), new ArrayList<>(), false);
    }

    public static ItemStack build(Player player, Material material, int data, int amount, String display, List<String> lore) {
        return build(player, material, 0, data, amount, display, lore, new ArrayList<>(), false);
    }

    public static ItemStack build(Player player, Material material, int data, int amount, String display, List<String> lore, List<Pair<String, String>> loreReplaceList) {
        return build(player, material, 0, data, amount, display, lore, loreReplaceList, false);
    }

    public static ItemStack build(Player player, Material material, int data, int amount, String display, List<String> lore, List<Pair<String, String>> loreReplaceList, boolean glow) {
        return build(player, material, 0, data, amount, display, lore, loreReplaceList, glow);
    }

    public static ItemStack build(Player player, Material material, int id, int data, int amount, String display, List<String> lore, List<Pair<String, String>> loreReplaceList, boolean glow) {
        ItemStack item = decideItem(material, id, data, amount);
        ItemMeta im = getItemMeta(item);
        for (Pair<String, String> pair : loreReplaceList) {
            display = display.replace(pair.getKey(), pair.getValue());
        }
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', display));
        List<String> loreList = new ArrayList<>();
        for (String s : lore) {
            for (Pair<String, String> pair : loreReplaceList) {
                s = s.replace(pair.getKey(), pair.getValue());
            }
            loreList.add(Util.handlePlaceholders(player, s));
        }
        if (!loreList.isEmpty()) im.setLore(loreList);
        if (glow) {
            im.addEnchant(Enchantment.DURABILITY, 1, true);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(im);
        return item;
    }

    public static void setItemAroundInv(Inventory inv, ItemStack item) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i >= inv.getSize() - 9 || i % 9 == 0 || (i + 1) % 9 == 0)
                inv.setItem(i, item);
        }
    }

    public static void setItemInLine(Inventory inv, ItemStack item, int line) {
        for (int i = 0; i < 9; i++) {
            inv.setItem((line - 1) * 9 + i, item);
        }
    }

    public static void setItemIn(Inventory inv, ItemStack item, int... slots) {
        for (int slot : slots) inv.setItem(slot, item);
    }

    public static ItemStack writeNBT(ItemStack item, String key, String value) {
        net.minecraft.server.v1_12_R1.ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbt = nmsitem.getTag();
        if (nbt == null) nbt = new NBTTagCompound();
        if (nbt.hasKey(key)) nbt.remove(key);
        nbt.set(key, new NBTTagString(value));
        nmsitem.setTag(nbt);
        return CraftItemStack.asBukkitCopy(nmsitem);
    }

    public static ItemStack writeNBT(ItemStack item, String key, int value) {
        net.minecraft.server.v1_12_R1.ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbt = nmsitem.getTag();
        if (nbt == null) nbt = new NBTTagCompound();
        if (nbt.hasKey(key)) nbt.remove(key);
        nbt.set(key, new NBTTagInt(value));
        nmsitem.setTag(nbt);
        return CraftItemStack.asBukkitCopy(nmsitem);
    }

    public static Optional<String> readNBTString(ItemStack item, String key) {
        net.minecraft.server.v1_12_R1.ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbt = nmsitem.getTag();
        if (nbt == null || !nbt.hasKeyOfType(key, 8)) return Optional.empty();
        NBTBase result = nbt.get(key);
        if (!(result instanceof NBTTagString)) return Optional.empty();
        return Optional.of(((NBTTagString) result).c_());
    }

    public static Optional<Integer> readNBTInt(ItemStack item, String key) {
        net.minecraft.server.v1_12_R1.ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nbt = nmsitem.getTag();
        if (nbt == null || !nbt.hasKeyOfType(key, 3)) return Optional.empty();
        NBTBase result = nbt.get(key);
        if (!(result instanceof NBTTagInt)) return Optional.empty();
        return Optional.of(((NBTTagInt) result).e());
    }

    public static Optional<Enchantment> getEnchantment(String name) {
        for (Enchantment ench : Enchantment.values()) {
            if (ench.getName().equalsIgnoreCase(name)) return Optional.of(ench);
        }
        return Optional.empty();
    }
}

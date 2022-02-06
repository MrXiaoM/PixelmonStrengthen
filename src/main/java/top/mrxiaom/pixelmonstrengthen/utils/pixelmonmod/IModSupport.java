package top.mrxiaom.pixelmonstrengthen.utils.pixelmonmod;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public interface IModSupport {
    enum IvsEvsStats {
        HP, Attack, Defence, SpecialAttack, SpecialDefence, Speed;

        public static IvsEvsStats get(int i) {
            switch (i) {
                case 0:
                    return HP;
                case 1:
                    return Attack;
                case 2:
                    return Defence;
                case 3:
                    return SpecialAttack;
                case 4:
                    return SpecialDefence;
                case 5:
                    return Speed;
                default:
                    return null;
            }
        }
    }

    default int getV(Pokemon pokemon) {
        int v = 0;
        if (getIvs(pokemon, IvsEvsStats.HP) == 31) v++;
        if (getIvs(pokemon, IvsEvsStats.Attack) == 31) v++;
        if (getIvs(pokemon, IvsEvsStats.Defence) == 31) v++;
        if (getIvs(pokemon, IvsEvsStats.SpecialAttack) == 31) v++;
        if (getIvs(pokemon, IvsEvsStats.SpecialDefence) == 31) v++;
        if (getIvs(pokemon, IvsEvsStats.Speed) == 31) v++;
        return v;
    }

    default Map<IvsEvsStats, Integer> getIvs(Pokemon pokemon) {
        Map<IvsEvsStats, Integer> result = new HashMap<>();
        for (IvsEvsStats stat : IvsEvsStats.values()) {
            result.put(stat, getIvs(pokemon, stat));
        }
        return result;
    }

    void setIvs(Pokemon pokemon, IvsEvsStats stats, int ivs);

    int getIvs(Pokemon pokemon, IvsEvsStats stats);

    String getPokemonBaseName(Pokemon pokemon);

    ItemStack toPokemonPhoto(ItemStack item, Pokemon pokemon);

    PlayerPartyStorage getPokemonParty(UUID uuid);

    @SuppressWarnings("all")
    static IModSupport decideVersion(Logger logger) {
        String version = getPixelmonVersion();
        if (logger != null) logger.info("宝可梦Mod版本: " + version);
        IModSupport modVersion;
        if(version.equalsIgnoreCase("8.3.6")) modVersion = new V836();
        else modVersion = new V820();
        logger.info("最终使用的宝可梦API: " + modVersion.getClass().getSimpleName());
        return modVersion;
    }
    static String getPixelmonVersion() {
        try {
            return (String) Pixelmon.class.getDeclaredField("VERSION").get(null);
        } catch (Throwable ignored) {
            return "8.2.0";
        }
    }
}

package top.mrxiaom.pixelmonstrengthen.utils.pixelmonmod;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.forms.EnumNoForm;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.pixelmonstrengthen.utils.ItemStackUtil;

import java.util.Optional;
import java.util.UUID;

public class V820 implements IModSupport {
    @Override
    public void setIvs(Pokemon pokemon, IvsEvsStats stats, int ivs) {
        if (pokemon == null) return;
        if (stats.equals(IvsEvsStats.HP)) pokemon.getIVs().setStat(StatsType.HP, ivs);
        if (stats.equals(IvsEvsStats.Attack)) pokemon.getIVs().setStat(StatsType.Attack, ivs);
        if (stats.equals(IvsEvsStats.Defence)) pokemon.getIVs().setStat(StatsType.Defence, ivs);
        if (stats.equals(IvsEvsStats.SpecialAttack)) pokemon.getIVs().setStat(StatsType.SpecialAttack, ivs);
        if (stats.equals(IvsEvsStats.SpecialDefence)) pokemon.getIVs().setStat(StatsType.SpecialDefence, ivs);
        if (stats.equals(IvsEvsStats.Speed)) pokemon.getIVs().setStat(StatsType.Speed, ivs);
    }

    public int getIvs(Pokemon pokemon, IvsEvsStats stats) {
        if (pokemon == null) return -1;
        if (stats.equals(IvsEvsStats.HP)) return pokemon.getIVs().getStat(StatsType.HP);
        if (stats.equals(IvsEvsStats.Attack)) return pokemon.getIVs().getStat(StatsType.Attack);
        if (stats.equals(IvsEvsStats.Defence)) return pokemon.getIVs().getStat(StatsType.Defence);
        if (stats.equals(IvsEvsStats.SpecialAttack)) return pokemon.getIVs().getStat(StatsType.SpecialAttack);
        if (stats.equals(IvsEvsStats.SpecialDefence)) return pokemon.getIVs().getStat(StatsType.SpecialDefence);
        if (stats.equals(IvsEvsStats.Speed)) return pokemon.getIVs().getStat(StatsType.Speed);
        return -1;
    }

    public String getPokemonBaseName(Pokemon pokemon) {
        return pokemon.getBaseStats().getPokemonName();
    }

    @Override
    public PlayerPartyStorage getPokemonParty(UUID uuid) {
        return Pixelmon.storageManager.getParty(uuid);
    }

    @Override
    public ItemStack toPokemonPhoto(ItemStack item, Pokemon pokemon) {
        item.setType(Material.valueOf("PIXELMON_PIXELMON_SPRITE"));

        String path = "pixelmon:sprites/";
        String extra = "";
        Optional<EnumSpecies> opti = EnumSpecies.getFromName(pokemon.getSpecies().name);
        if (opti.isPresent()) {
            EnumSpecies pixelmon = opti.get();
            if (pixelmon.getFormEnum(pokemon.getForm()) != EnumNoForm.NoForm) {
                extra = pixelmon.getFormEnum(pokemon.getForm()).getSpriteSuffix(pokemon.isShiny());
            }
            if (EnumSpecies.mfSprite.contains(pixelmon)) {
                extra = "-" + pokemon.getGender().name().toLowerCase();
            }
        }
        path = path + "pokemon/" + pokemon.getSpecies().getNationalPokedexNumber() + extra;
        return ItemStackUtil.writeNBT(item, "SpriteName", path);
    }
}

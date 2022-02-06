package top.mrxiaom.pixelmonstrengthen.utils.pixelmonmod;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.forms.EnumNoForm;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import top.mrxiaom.pixelmonstrengthen.utils.ItemStackUtil;

import java.util.Optional;

public class V836 extends V820{

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
        }
        path = path + "pokemon/" + pokemon.getSpecies().getNationalPokedexNumber() + extra;
        return ItemStackUtil.writeNBT(item, "SpriteName", path);
    }
}

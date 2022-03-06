package top.mrxiaom.pixelmonstrengthen;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import top.mrxiaom.pixelmonstrengthen.gui.GuiManager;
import top.mrxiaom.pixelmonstrengthen.utils.pixelmonmod.IModSupport;
import top.mrxiaom.pixelmonstrengthen.utils.ItemStackUtil;

import java.util.Optional;

public final class PixelmonStrengthen extends JavaPlugin {
    private static PixelmonStrengthen INSTANCE;
    IModSupport modSupport;
    GuiManager guiManager;
    Commands commands;
    Config pluginConfig;
    PlayerData playerData;
    OtherPlugin otherPlugin;

    @Override
    public void onEnable() {
        modSupport = IModSupport.decideVersion(getLogger());
        guiManager = new GuiManager(this);
        commands = new Commands(this);
        pluginConfig = new Config(this);
        playerData = new PlayerData(this);
        this.reloadConfig();
        otherPlugin = new OtherPlugin(this.getLogger());
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        guiManager.closeAllGui();
    }

    public void reloadConfig() {
        super.saveDefaultConfig();
        super.reloadConfig();
        pluginConfig.reloadConfig();
        playerData.reloadAllConfig();
    }

    public Optional<Integer> checkSoul(ItemStack item, Pokemon pokemon) {
        if (item == null || pokemon == null) return Optional.empty();
        String baseName = modSupport.getPokemonBaseName(pokemon);
        String pokemonSoulType = ItemStackUtil.readNBTString(item, "PokemonSoulType").orElse(null);
        return pokemonSoulType != null && pokemonSoulType.equalsIgnoreCase(baseName) ?
                ItemStackUtil.readNBTInt(item, "PokemonSoulIvs") : Optional.empty();
    }

    public boolean isSoul(ItemStack item) {
        if (item == null) return false;
        String pokemonSoulType = ItemStackUtil.readNBTString(item, "PokemonSoulType").orElse(null);
        return pokemonSoulType != null;
    }

    public Config getPluginConfig() {
        return pluginConfig;
    }

    public PlayerData getPlayerData() { return playerData; }

    public void setModSupport(IModSupport modSupport) {
        this.modSupport = modSupport;
    }

    public IModSupport getModSupport() {
        return modSupport;
    }

    public static PixelmonStrengthen getInstance() {
        return INSTANCE;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public OtherPlugin getOtherPlugin() {
        return otherPlugin;
    }
}

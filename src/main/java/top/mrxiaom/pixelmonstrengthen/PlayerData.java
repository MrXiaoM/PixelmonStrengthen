package top.mrxiaom.pixelmonstrengthen;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlayerData {
    public static class Data {
        PlayerData data;
        String name;
        Map<String, Integer> storage;
        private Data(PlayerData data, String name, Map<String, Integer> storage) {
            this.data = data;
            this.name = name;
            this.storage = storage;
        }

        public Data(PlayerData data, String name) {
            this(data, name, new HashMap<>());
        }

        public String getName() {
            return name;
        }

        public Map<String, Integer> getStorage() {
            return storage;
        }

        public Optional<Integer> getStorage(String id) {
            if (!this.storage.containsKey(id)) return Optional.empty();
            return Optional.of(this.storage.getOrDefault(id, 0));
        }

        public Data putStorage(String id, int amount) {
            this.storage.put(id, amount);
            return this;
        }

        public static Optional<Data> loadFromFile(PlayerData data, File file) {
            if (data == null || file == null || !file.exists() || !file.getName().toLowerCase().endsWith(".yml")) return Optional.empty();
            String name = file.getName().substring(0, file.getName().length() - 4);
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                Map<String, Integer> storage = new HashMap<>();
                ConfigurationSection section = config.getConfigurationSection("storage");
                if (section != null) {
                    for (String key : section.getKeys(false)){
                        int amount = section.getInt(key, 0);
                        if (amount > 0) {
                            storage.put(key, amount);
                        }
                    }
                }
                return Optional.of(new Data(data, name, storage));
            } catch (Throwable t) {
                data.plugin.getLogger().warning("载入玩家数据 " + name + " 时出现异常");
                t.printStackTrace();
                return Optional.empty();
            }
        }

        public Data saveToFile() {
            return saveToFile(new File(data.configDir, name + ".yml"));
        }

        public Data saveToFile(File file) {
            try {
                YamlConfiguration config = this.saveToYaml();
                if (config.getKeys(false).size() > 0) {
                    config.save(file);
                }
            } catch (Throwable t) {
                data.plugin.getLogger().warning("保存玩家数据 " + name + "时出现异常");
                t.printStackTrace();
            }
            return this;
        }

        public YamlConfiguration saveToYaml() {
            YamlConfiguration config = new YamlConfiguration();
            for (String key : storage.keySet()) {
                config.set("storage." + key, storage.get(key));
            }
            return config;
        }
    }

    File configDir;
    PixelmonStrengthen plugin;
    Map<String, Data> playerData = new HashMap<>();
    public PlayerData(PixelmonStrengthen plugin) {
        this.plugin = plugin;
        this.configDir = new File(plugin.getDataFolder(), "players");
    }

    public Optional<Data> get(String playerName) {
        if (!this.playerData.containsKey(playerName.toLowerCase())) return Optional.empty();
        return Optional.of(this.playerData.get(playerName.toLowerCase()));
    }

    public PlayerData put(Data data) {
        this.playerData.put(data.getName().toLowerCase(), data);
        return this;
    }

    public PlayerData reloadAllConfig() {
        try {
            this.playerData.clear();
            File[] files = configDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    Optional<Data> result = Data.loadFromFile(this, file);
                    if (result.isPresent()) {
                        Data data = result.get();
                        this.put(data);
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return this;
    }

    public PlayerData saveAllConfig() {
        for (Data data : playerData.values()) {
            data.saveToFile();
        }
        return this;
    }
}

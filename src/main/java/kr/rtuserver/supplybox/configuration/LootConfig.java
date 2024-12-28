package kr.rtuserver.supplybox.configuration;

import com.google.common.io.Files;
import kr.rtuserver.framework.bukkit.api.config.RSConfiguration;
import kr.rtuserver.framework.bukkit.api.utility.platform.FileResource;
import kr.rtuserver.supplybox.RSSupplyBox;
import kr.rtuserver.supplybox.loot.Loot;
import org.simpleyaml.configuration.ConfigurationSection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LootConfig {

    private final Map<String, Loot> map = new HashMap<>();
    private final RSSupplyBox plugin;

    public LootConfig(RSSupplyBox plugin) {
        this.plugin = plugin;
        reload();
    }

    public Loot get(String name) {
        return map.get(name);
    }

    public void reload() {
        map.clear();
        if (!new File(plugin.getDataFolder() + "/Configs/Loots/").exists())
            FileResource.createFileCopy(plugin, "Configs/Loots", "Example.yml");
        File[] files = FileResource.createFolder(plugin.getDataFolder() + "/Configs/Loots").listFiles();
        if (files == null) return;
        for (File file : files) {
            String name = file.getName();
            if (!name.endsWith(".yml")) continue;
            Config config = new Config(name);
        }
    }


    class Config extends RSConfiguration<RSSupplyBox> {

        private final String name;

        public Config(String name) {
            super(plugin, "Configs/Loots", name, null);
            this.name = Files.getNameWithoutExtension(name);
            setup(this);
        }

        private void init() {
            Loot loot = new Loot(name);
            loot.setSelectMin(getInt("select.min", 2));
            loot.setSelectMax(getInt("select.max", 2));

            ConfigurationSection section = getConfigurationSection("items");
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    Loot.Item item = new Loot.Item(key);
                    item.setChance(getInt("items." + key + ".change", 100));
                    item.setMin(getInt("items." + key + ".min", 1));
                    item.setMax(getInt("items." + key + ".max", 64));
                    loot.addItem(item);
                }
            }
            map.put(name, loot);
        }
    }
}

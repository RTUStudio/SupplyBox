package com.github.ipecter.rtustudio.supplybox.configuration;

import com.github.ipecter.rtustudio.supplybox.SupplyBox;
import com.github.ipecter.rtustudio.supplybox.loot.Loot;
import com.google.common.io.Files;
import kr.rtuserver.framework.bukkit.api.configuration.RSConfiguration;
import kr.rtuserver.framework.bukkit.api.utility.platform.FileResource;
import kr.rtuserver.yaml.configuration.ConfigurationSection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LootConfig {

    private final Map<String, Loot> map = new HashMap<>();
    private final SupplyBox plugin;

    public LootConfig(SupplyBox plugin) {
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


    class Config extends RSConfiguration<SupplyBox> {

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
                    item.setWeight(getInt("items." + key + ".weight", 100));
                    item.setMin(getInt("items." + key + ".min", 1));
                    item.setMax(getInt("items." + key + ".max", 64));
                    loot.addItem(item);
                }
            }
            map.put(name, loot);
        }
    }

}

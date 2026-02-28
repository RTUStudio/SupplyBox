package kr.rtustudio.supplybox.configuration;

import com.google.common.io.Files;
import kr.rtustudio.framework.bukkit.api.configuration.RSConfiguration;
import kr.rtustudio.framework.bukkit.api.platform.FileResource;
import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.loot.Loot;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"FieldMayBeFinal", "unused"})
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

    public Map<String, Loot> getMap() {
        return map;
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
            new Config(name);
        }
    }

    class Config extends RSConfiguration.Wrapper<SupplyBox> {

        private final String id;

        public Config(String name) {
            super(plugin, "Configs/Loots", name, null);
            this.id = Files.getNameWithoutExtension(name);
            setup(this);
        }

        private void init() {
            Loot loot = new Loot(id);
            loot.setSelectMin(getInt("select.min", 2, """
                    Minimum number of items to select
                    선택 최소 아이템 수"""));
            loot.setSelectMax(getInt("select.max", 2, """
                    Maximum number of items to select
                    선택 최대 아이템 수"""));
            for (String key : keys("items")) {
                String path = "items." + key;
                String itemId = getString(path + ".item", "", """
                        Item identifier (e.g. minecraft:stone, nexo:id)
                        아이템 식별자""");
                if (itemId.isEmpty()) continue;
                Loot.Item item = new Loot.Item(itemId);
                item.setWeight(getInt(path + ".weight", 100, """
                        Weighted chance (higher = more likely)
                        가중치 (높을수록 확률 증가)"""));
                item.setMin(getInt(path + ".min", 1, """
                        Minimum amount
                        최소 수량"""));
                item.setMax(getInt(path + ".max", 64, """
                        Maximum amount
                        최대 수량"""));
                loot.addItem(item);
            }
            map.put(id, loot);
        }
    }
}

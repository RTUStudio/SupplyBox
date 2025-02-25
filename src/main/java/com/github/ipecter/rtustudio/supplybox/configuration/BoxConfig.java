package com.github.ipecter.rtustudio.supplybox.configuration;

import com.github.ipecter.rtustudio.supplybox.SupplyBox;
import com.github.ipecter.rtustudio.supplybox.box.Box;
import com.github.ipecter.rtustudio.supplybox.loot.Loot;
import com.google.common.io.Files;
import kr.rtuserver.framework.bukkit.api.config.RSConfiguration;
import kr.rtuserver.framework.bukkit.api.utility.platform.FileResource;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoxConfig {

    @Getter
    private final Map<String, Box> map = new HashMap<>();
    private final SupplyBox plugin;

    public BoxConfig(SupplyBox plugin) {
        this.plugin = plugin;
        reload();
    }

    public Box get(String name) {
        return map.get(name);
    }

    public void reload() {
        map.clear();
        if (!new File(plugin.getDataFolder() + "/Configs/Boxes/").exists())
            FileResource.createFileCopy(plugin, "Configs/Boxes", "Example.yml");
        File[] files = FileResource.createFolder(plugin.getDataFolder() + "/Configs/Boxes").listFiles();
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
            super(plugin, "Configs/Boxes", name, null);
            this.name = Files.getNameWithoutExtension(name);
            setup(this);
        }

        private void init() {
            Box box = new Box(name);
            box.setDisplay(getString("displayName", box.getName()));
            box.setAlertMinecraft(getBoolean("alert.minecraft", box.isAlertMinecraft()));
            box.setAlertDiscord(getBoolean("alert.discord", box.isAlertDiscord()));
            box.setBlock(getString("block", "chest"));
            String itemBox = getString("item.box", "");
            if (itemBox.isEmpty()) return;
            box.setItemBox(itemBox);
            String itemKey = getString("item.key", "");
            box.setItemKey(itemKey);
            String intercatStr = getString("interact", "drop").toUpperCase();
            if (!List.of("DROP", "INVENTORY", "GIVE").contains(intercatStr)) intercatStr = "DROP";
            box.setInteract(Box.Interact.valueOf(intercatStr));
            String lootStr = getString("loot", "");
            Loot loot = plugin.getLootConfig().get(lootStr);
            if (loot != null) box.setLoot(loot);
            else {
                plugin.console("Loot: " + lootStr + " is not found. (" + name + ")");
                plugin.console("Loot: " + lootStr + "을(를) 찾을 수 없습니다. (" + name + ")");
                return;
            }
            map.put(name, box);
        }
    }
}

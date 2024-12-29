package kr.rtuserver.supplybox.configuration;

import com.google.common.io.Files;
import kr.rtuserver.framework.bukkit.api.config.RSConfiguration;
import kr.rtuserver.framework.bukkit.api.utility.platform.FileResource;
import kr.rtuserver.supplybox.RSSupplyBox;
import kr.rtuserver.supplybox.box.Box;
import kr.rtuserver.supplybox.loot.Loot;
import lombok.Getter;

import java.io.File;
import java.util.*;

public class BoxConfig {

    @Getter
    private final Map<String, Box> map = new HashMap<>();
    private final RSSupplyBox plugin;

    public BoxConfig(RSSupplyBox plugin) {
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


    class Config extends RSConfiguration <RSSupplyBox>{

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
            box.setTime(getInt("time", box.getTime()));
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

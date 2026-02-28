package kr.rtustudio.supplybox.configuration;

import com.google.common.io.Files;
import kr.rtustudio.framework.bukkit.api.configuration.RSConfiguration;
import kr.rtustudio.framework.bukkit.api.platform.FileResource;
import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.box.Box;
import kr.rtustudio.supplybox.loot.Loot;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@SuppressWarnings({"FieldMayBeFinal", "unused"})
public class BoxConfig {

    private final Map<String, Box> map = new HashMap<>();
    private final SupplyBox plugin;
    private final LootConfig lootConfig;

    public BoxConfig(SupplyBox plugin) {
        this.plugin = plugin;
        this.lootConfig = new LootConfig(plugin);
        reload();
    }

    public Box get(String name) {
        return map.get(name);
    }

    public void reload() {
        map.clear();
        lootConfig.reload();
        if (!new File(plugin.getDataFolder() + "/Configs/Boxes/").exists())
            FileResource.createFileCopy(plugin, "Configs/Boxes", "Example.yml");
        File[] files = FileResource.createFolder(plugin.getDataFolder() + "/Configs/Boxes").listFiles();
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
            super(plugin, "Configs/Boxes", name, null);
            this.id = Files.getNameWithoutExtension(name);
            setup(this);
        }

        private void init() {
            Box box = new Box(id);
            box.setEnabled(getBoolean("enabled", false, """
                    Whether this box is enabled
                    이 보급 상자를 활성화할지 여부"""));
            String displayName = getString("displayName", "", """
                    Display name of the box
                    보급 상자의 표시 이름""");
            box.setDisplay(displayName.isEmpty() ? id : displayName);
            box.setAlertMinecraft(getBoolean("alert.minecraft", true, """
                    Send spawn notifications in Minecraft
                    마인크래프트 내 알림 전송 여부"""));
            box.setAlertDiscord(getBoolean("alert.discord", true, """
                    Send spawn notifications to Discord
                    디스코드 알림 전송 여부"""));
            box.setBlock(getString("block", "chest", """
                    Block type for the supply box
                    보급 상자 블록 타입"""));
            box.setItemBox(getString("item.box", "", """
                    Custom item identifier for the box
                    상자 커스텀 아이템 식별자"""));
            box.setItemKey(getString("item.key", "", """
                    Custom item identifier for the key
                    열쇠 커스텀 아이템 식별자"""));
            String interactStr = getString("interact", "DROP", """
                    Interaction method: DROP, INVENTORY, or GIVE
                    상호작용 방법""").toUpperCase();
            if (!List.of("DROP", "INVENTORY", "GIVE").contains(interactStr)) interactStr = "DROP";
            box.setInteract(Box.Interact.valueOf(interactStr));
            String lootName = getString("loot", "", """
                    Loot table name to use
                    사용할 전리품 테이블 이름""");
            Loot loot = lootConfig.get(lootName);
            if (loot != null) {
                box.setLoot(loot);
            } else {
                plugin.console("Loot: " + lootName + " is not found. (" + id + ")");
                plugin.console("Loot: " + lootName + "을(를) 찾을 수 없습니다. (" + id + ")");
                return;
            }
            map.put(id, box);
        }
    }
}

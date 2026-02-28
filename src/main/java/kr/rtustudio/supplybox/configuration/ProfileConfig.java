package kr.rtustudio.supplybox.configuration;

import com.google.common.io.Files;
import kr.rtustudio.framework.bukkit.api.configuration.RSConfiguration;
import kr.rtustudio.framework.bukkit.api.platform.FileResource;
import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.data.BlockPos;
import kr.rtustudio.supplybox.data.WorldCoordinate;
import kr.rtustudio.supplybox.profile.LocationProfile;
import kr.rtustudio.supplybox.profile.Profile;
import kr.rtustudio.supplybox.profile.RegionProfile;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@SuppressWarnings({"FieldMayBeFinal", "unused"})
public class ProfileConfig {

    private final Map<String, Profile> map = new HashMap<>();
    private final SupplyBox plugin;

    public ProfileConfig(SupplyBox plugin) {
        this.plugin = plugin;
        reload();
    }

    public Profile get(String name) {
        return map.get(name);
    }

    public void reload() {
        map.clear();
        if (!new File(plugin.getDataFolder() + "/Configs/Profiles/").exists()) {
            FileResource.createFileCopy(plugin, "Configs/Profiles", "Example_Region.yml");
            FileResource.createFileCopy(plugin, "Configs/Profiles", "Example_Location.yml");
        }
        File[] files = FileResource.createFolder(plugin.getDataFolder() + "/Configs/Profiles").listFiles();
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
            super(plugin, "Configs/Profiles", name, null);
            this.id = Files.getNameWithoutExtension(name);
            setup(this);
        }

        private void init() {
            if (!isNull("location")) {
                LocationProfile profile = new LocationProfile(id);
                profile.setSelect(getInt("location.select", 1, """
                        Number of locations to select
                        선택할 위치의 수"""));
                List<WorldCoordinate> list = new ArrayList<>();
                for (String str : getStringList("location.list", List.of(), """
                        List of world,x,y,z values
                        world,x,y,z 형식의 목록""")) {
                    String[] split = str.split(",");
                    if (split.length < 3) continue;
                    if (split.length == 3) {
                        int x = Integer.parseInt(split[0].trim());
                        int z = Integer.parseInt(split[2].trim());
                        list.add(new WorldCoordinate("world", new BlockPos(x, null, z)));
                    } else {
                        int x = Integer.parseInt(split[1].trim());
                        int y = Integer.parseInt(split[2].trim());
                        int z = Integer.parseInt(split[3].trim());
                        list.add(new WorldCoordinate(split[0].trim(), new BlockPos(x, y, z)));
                    }
                }
                profile.setLocations(list);
                map.put(id, profile);
            } else if (!isNull("region")) {
                RegionProfile profile = new RegionProfile(id);
                profile.setWorld(getString("region.world", "", """
                        World name
                        월드 이름"""));
                profile.setCenterX(getInt("region.center.x", 0, """
                        Center X coordinate
                        중심 X 좌표"""));
                profile.setCenterZ(getInt("region.center.z", 0, """
                        Center Z coordinate
                        중심 Z 좌표"""));
                profile.setRadius(getInt("region.radius", 100, """
                        Radius of the region
                        영역 반지름"""));
                String typeStr = getString("region.type", "SQUARE", """
                        Region shape: SQUARE or CIRCLE
                        영역 형태""").toUpperCase();
                profile.setType(List.of("SQUARE", "CIRCLE").contains(typeStr)
                        ? RegionProfile.Type.valueOf(typeStr)
                        : RegionProfile.Type.SQUARE);
                profile.setAmount(getInt("region.amount", 1, """
                        Number of points to generate per spawn
                        스폰 시 생성할 지점 수"""));
                map.put(id, profile);
            }
        }
    }
}

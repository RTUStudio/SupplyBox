package com.github.ipecter.rtustudio.supplybox.configuration;

import com.github.ipecter.rtustudio.supplybox.SupplyBox;
import com.github.ipecter.rtustudio.supplybox.data.BlockCoordinate;
import com.github.ipecter.rtustudio.supplybox.data.WorldCoordinate;
import com.github.ipecter.rtustudio.supplybox.profile.LocationProfile;
import com.github.ipecter.rtustudio.supplybox.profile.Profile;
import com.github.ipecter.rtustudio.supplybox.profile.RegionProfile;
import com.google.common.io.Files;
import kr.rtuserver.framework.bukkit.api.configuration.RSConfiguration;
import kr.rtuserver.framework.bukkit.api.platform.FileResource;
import kr.rtuserver.framework.yaml.configuration.ConfigurationSection;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileConfig {

    @Getter
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
            Config config = new Config(name);
        }
    }


    class Config extends RSConfiguration<SupplyBox> {

        private final String name;

        public Config(String name) {
            super(plugin, "Configs/Profiles", name, null);
            this.name = Files.getNameWithoutExtension(name);
            setup(this);
        }

        private void init() {
            ConfigurationSection locSection = getConfigurationSection("location");
            ConfigurationSection regSection = getConfigurationSection("region");
            if (locSection != null) {
                LocationProfile profile = new LocationProfile(name);
                profile.setSelect(getInt("location.select", 1));
                List<WorldCoordinate> list = new ArrayList<>();
                for (String str : getStringList("location.list", List.of())) {
                    String[] split = str.split(",");
                    if (split.length != 4) continue;
                    int x = Integer.parseInt(split[1]);
                    int y = Integer.parseInt(split[2]);
                    int z = Integer.parseInt(split[3]);
                    BlockCoordinate pos = new BlockCoordinate(x, y, z);
                    list.add(new WorldCoordinate(split[0], pos));
                }
                profile.setLocations(list);
                map.put(name, profile);
            } else if (regSection != null) {
                RegionProfile profile = new RegionProfile(name);
                profile.setWorld(getString("region.world", ""));
                profile.setCenterX(getInt("region.center.x", 0));
                profile.setCenterZ(getInt("region.center.z", 0));
                profile.setRadius(getInt("region.radius", 100));
                String typeStr = getString("region.type", "square").toUpperCase();
                if (List.of("SQUARE", "CIRCLE").contains(typeStr)) {
                    RegionProfile.Type type = RegionProfile.Type.valueOf(typeStr);
                    profile.setType(type);
                } else profile.setType(RegionProfile.Type.SQUARE);
                profile.setAmount(getInt("region.amount", 1));
                map.put(name, profile);
            }
        }
    }

}

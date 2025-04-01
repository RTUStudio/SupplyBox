package com.github.ipecter.rtustudio.supplybox.configuration;

import com.github.ipecter.rtustudio.supplybox.SupplyBox;
import com.github.ipecter.rtustudio.supplybox.box.Box;
import com.github.ipecter.rtustudio.supplybox.profile.Profile;
import com.github.ipecter.rtustudio.supplybox.schedule.Schedule;
import kr.rtuserver.framework.bukkit.api.configuration.RSConfiguration;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ScheduleConfig extends RSConfiguration<SupplyBox> {

    private final SupplyBox plugin;
    private final Map<String, Schedule> map = new HashMap<>();

    public ScheduleConfig(SupplyBox plugin) {
        super(plugin, "Schedule.yml", null);
        this.plugin = plugin;
        setup(this);
    }

    private void init() {
        map.clear();
        for (String key : getConfig().getKeys(false)) {
            boolean enable = getBoolean(key + ".enable", false);
            int period = getInt(key + ".period", 60);
            String boxStr = getString(key + ".box", "");
            String profileStr = getString(key + ".profile", "");
            Box box = plugin.getBoxConfig().get(boxStr);
            if (box == null) {
                plugin.console("Box: " + boxStr + " is not found. (Schedule.yml)");
                plugin.console("Box: " + boxStr + "을(를) 찾을 수 없습니다. (Schedule.yml)");
                continue;
            }
            Profile profile = plugin.getProfileConfig().get(profileStr);
            if (profile == null) {
                plugin.console("Profile: " + profileStr + " is not found. (Schedule.yml)");
                plugin.console("Profile: " + profileStr + "을(를) 찾을 수 없습니다. (Schedule.yml)");
                continue;
            }
            map.put(key, new Schedule(key, enable, period, box, profile));
        }
    }

}

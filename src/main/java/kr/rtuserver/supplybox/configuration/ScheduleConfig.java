package kr.rtuserver.supplybox.configuration;

import kr.rtuserver.framework.bukkit.api.config.RSConfiguration;
import kr.rtuserver.supplybox.RSSupplyBox;
import kr.rtuserver.supplybox.box.Box;
import kr.rtuserver.supplybox.profile.Profile;
import kr.rtuserver.supplybox.schedule.Schedule;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ScheduleConfig extends RSConfiguration<RSSupplyBox> {

    private final RSSupplyBox plugin;
    private final Map<String, Schedule> map = new HashMap<>();

    public ScheduleConfig(RSSupplyBox plugin) {
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

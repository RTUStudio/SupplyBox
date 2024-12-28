package kr.rtuserver.supplybox.schedule;

import kr.rtuserver.framework.bukkit.api.utility.scheduler.CraftScheduler;
import kr.rtuserver.supplybox.RSSupplyBox;
import kr.rtuserver.supplybox.box.BoxManager;
import kr.rtuserver.supplybox.configuration.ScheduleConfig;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class ScheduleManager {

    private final RSSupplyBox plugin;
    private final ScheduleConfig scheduleConfig;
    private final BoxManager boxManager;

    private final Map<String, BukkitTask> tasks = new HashMap<>();

    public ScheduleManager(RSSupplyBox plugin) {
        this.plugin = plugin;
        this.scheduleConfig = plugin.getScheduleConfig();
        this.boxManager = plugin.getBoxManager();
    }

    public boolean add(String name) {
        Schedule schedule = scheduleConfig.getMap().get(name);
        if (schedule != null) {
            if (!tasks.containsKey(name)) {
                BukkitTask task = CraftScheduler.runTimerAsync(plugin, () -> {
                    boxManager.spawn(schedule.box(), schedule.profile());
                }, 0, schedule.period());
                tasks.put(name, task);
            } else return false;
        } else return false;
        return true;
    }

    public boolean remove(String name) {
        Schedule schedule = scheduleConfig.getMap().get(name);
        if (schedule != null) {
            if (tasks.containsKey(name)) {
                tasks.remove(name).cancel();
            } else return false;
        } else return false;
        return true;
    }

    public void start() {
        for (String name : scheduleConfig.getMap().keySet()) {
            Schedule schedule = scheduleConfig.getMap().get(name);
            System.out.println(schedule);
            if (!schedule.enable()) continue;
            int period = schedule.period();
            tasks.put(name, CraftScheduler.runTimerAsync(plugin, () -> {
                boxManager.spawn(schedule.box(), schedule.profile());
            }, period, period));
        }
    }

    public void stop() {
        for (String name : tasks.keySet()) tasks.remove(name).cancel();
    }
}

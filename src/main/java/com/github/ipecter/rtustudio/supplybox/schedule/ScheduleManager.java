package com.github.ipecter.rtustudio.supplybox.schedule;

import com.github.ipecter.rtustudio.supplybox.SupplyBox;
import com.github.ipecter.rtustudio.supplybox.box.BoxManager;
import com.github.ipecter.rtustudio.supplybox.configuration.ScheduleConfig;
import kr.rtuserver.framework.bukkit.api.core.scheduler.ScheduledTask;
import kr.rtuserver.framework.bukkit.api.scheduler.CraftScheduler;

import java.util.HashMap;
import java.util.Map;

public class ScheduleManager {

    private final SupplyBox plugin;
    private final ScheduleConfig scheduleConfig;
    private final BoxManager boxManager;

    private final Map<String, ScheduledTask> tasks = new HashMap<>();

    public ScheduleManager(SupplyBox plugin) {
        this.plugin = plugin;
        this.scheduleConfig = plugin.getScheduleConfig();
        this.boxManager = plugin.getBoxManager();
    }

    public boolean add(String name) {
        Schedule schedule = scheduleConfig.getMap().get(name);
        if (schedule != null) {
            if (!tasks.containsKey(name)) {
                ScheduledTask task = CraftScheduler.runTimerAsync(plugin, () -> {
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

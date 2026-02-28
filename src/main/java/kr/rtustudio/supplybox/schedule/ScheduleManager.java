package kr.rtustudio.supplybox.schedule;

import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.box.Box;
import kr.rtustudio.supplybox.box.BoxManager;
import kr.rtustudio.supplybox.configuration.ScheduleConfig;
import kr.rtustudio.supplybox.configuration.ScheduleConfig.Entry;
import kr.rtustudio.supplybox.profile.Profile;
import kr.rtustudio.framework.bukkit.api.core.scheduler.ScheduledTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScheduleManager {

    private final SupplyBox plugin;
    private final BoxManager boxManager;

    private final Map<String, ScheduledTask> tasks = new HashMap<>();
    private final Set<String> waitingForOpen = new HashSet<>();

    public ScheduleManager(SupplyBox plugin) {
        this.plugin = plugin;
        this.boxManager = plugin.getBoxManager();
        this.boxManager.setAllBoxesOpenedCallback(this::onAllBoxesOpened);
    }

    public boolean add(String name) {
        ScheduleConfig scheduleConfig = plugin.getConfiguration(ScheduleConfig.class);
        if (scheduleConfig == null) return false;
        Entry entry = scheduleConfig.getSchedules().get(name);
        if (entry == null || tasks.containsKey(name) || waitingForOpen.contains(name)) return false;
        startSchedule(name, entry);
        return true;
    }

    public boolean remove(String name) {
        waitingForOpen.remove(name);
        ScheduledTask task = tasks.remove(name);
        if (task == null) return false;
        task.cancel();
        return true;
    }

    public void start() {
        ScheduleConfig scheduleConfig = plugin.getConfiguration(ScheduleConfig.class);
        if (scheduleConfig == null) return;
        for (Map.Entry<String, Entry> ent : scheduleConfig.getSchedules().entrySet()) {
            String name = ent.getKey();
            Entry entry = ent.getValue();
            if (!entry.isEnabled()) continue;
            startSchedule(name, entry);
        }
    }

    public void stop() {
        tasks.values().forEach(ScheduledTask::cancel);
        tasks.clear();
        waitingForOpen.clear();
        boxManager.clearScheduleTracking();
    }

    private void startSchedule(String name, Entry entry) {
        long delay = Math.max(1L, entry.getPeriod()) * 20L;
        if (entry.isWaitForOpen()) {
            ScheduledTask task = plugin.getFramework().getScheduler().delay(() -> {
                tasks.remove(name);
                executeSpawn(name, entry);
                waitingForOpen.add(name);
            }, delay, true);
            tasks.put(name, task);
        } else {
            ScheduledTask task = plugin.getFramework().getScheduler().repeat(
                    () -> executeSpawn(name, entry), delay, true);
            tasks.put(name, task);
        }
    }

    private void executeSpawn(String name, Entry entry) {
        Box box = plugin.getBoxConfig().get(entry.getBox());
        Profile profile = plugin.getProfileConfig().get(entry.getProfile());
        if (box != null && box.isEnabled() && profile != null) {
            boxManager.spawn(box, profile, entry.isWaitForOpen() ? name : null);
        }
    }

    private void onAllBoxesOpened(String scheduleName) {
        if (!waitingForOpen.remove(scheduleName)) return;
        ScheduleConfig scheduleConfig = plugin.getConfiguration(ScheduleConfig.class);
        if (scheduleConfig == null) return;
        Entry entry = scheduleConfig.getSchedules().get(scheduleName);
        if (entry == null || !entry.isEnabled() || !entry.isWaitForOpen()) return;
        startSchedule(scheduleName, entry);
    }
}

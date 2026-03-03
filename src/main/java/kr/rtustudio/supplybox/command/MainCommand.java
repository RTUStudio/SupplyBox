package kr.rtustudio.supplybox.command;

import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.configuration.BoxConfig;
import kr.rtustudio.supplybox.configuration.ProfileConfig;
import kr.rtustudio.supplybox.configuration.QueueConfig;
import kr.rtustudio.supplybox.configuration.ScheduleConfig;
import kr.rtustudio.supplybox.schedule.ScheduleManager;
import kr.rtustudio.framework.bukkit.api.command.RSCommand;
import kr.rtustudio.framework.bukkit.api.command.RSCommandData;

public class MainCommand extends RSCommand<SupplyBox> {

    private final BoxConfig boxConfig;
    private final ProfileConfig profileConfig;
    private final QueueConfig queueConfig;
    private final ScheduleManager scheduleManager;

    public MainCommand(SupplyBox plugin) {
        super(plugin, "supplybox");
        this.boxConfig = plugin.getBoxConfig();
        this.profileConfig = plugin.getProfileConfig();
        this.queueConfig = plugin.getQueueConfig();
        this.scheduleManager = plugin.getScheduleManager();
        registerCommand(new SpawnCommand(plugin)); registerCommand(new DebugCommand(plugin));
    }

    @Override
    protected void reload(RSCommandData data) {
        scheduleManager.stop();
        boxConfig.reload();
        profileConfig.reload();
        getPlugin().reloadConfiguration(ScheduleConfig.class);
        queueConfig.reload();
        scheduleManager.start();
    }

}

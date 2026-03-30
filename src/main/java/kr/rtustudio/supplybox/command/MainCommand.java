package kr.rtustudio.supplybox.command;

import kr.rtustudio.framework.bukkit.api.command.CommandArgs;
import kr.rtustudio.framework.bukkit.api.command.RSCommand;
import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.configuration.*;
import kr.rtustudio.supplybox.schedule.ScheduleManager;

public class MainCommand extends RSCommand<SupplyBox> {

    private final QueueConfig queueConfig;
    private final ScheduleManager scheduleManager;

    public MainCommand(SupplyBox plugin) {
        super(plugin, "supplybox");
        this.queueConfig = plugin.getQueueConfig();
        this.scheduleManager = plugin.getScheduleManager();
        registerCommand(new SpawnCommand(plugin));
        registerCommand(new DebugCommand(plugin));
    }

    @Override
    protected void reload(CommandArgs data) {
        scheduleManager.stop();
        plugin.reloadConfiguration(LootConfig.class);
        plugin.reloadConfiguration(BoxConfig.class);
        plugin.reloadConfiguration(ProfileConfig.class);
        plugin.reloadConfiguration(ScheduleConfig.class);
        queueConfig.reload();
        plugin.getBoxManager().loadSchedules();
        scheduleManager.start();
    }

}

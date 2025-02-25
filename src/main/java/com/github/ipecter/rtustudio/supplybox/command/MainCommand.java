package com.github.ipecter.rtustudio.supplybox.command;

import com.github.ipecter.rtustudio.supplybox.SupplyBox;
import com.github.ipecter.rtustudio.supplybox.box.BoxManager;
import com.github.ipecter.rtustudio.supplybox.configuration.*;
import com.github.ipecter.rtustudio.supplybox.schedule.ScheduleManager;
import kr.rtuserver.framework.bukkit.api.command.RSCommand;
import kr.rtuserver.framework.bukkit.api.command.RSCommandData;

public class MainCommand extends RSCommand<SupplyBox> {

    private final LootConfig lootConfig;
    private final BoxConfig boxConfig;
    private final ProfileConfig profileConfig;
    private final ScheduleConfig scheduleConfig;
    private final QueueConfig queueConfig;

    private final BoxManager boxManager;
    private final ScheduleManager scheduleManager;

    public MainCommand(SupplyBox plugin) {
        super(plugin, "supplybox");
        this.lootConfig = plugin.getLootConfig();
        this.boxConfig = plugin.getBoxConfig();
        this.profileConfig = plugin.getProfileConfig();
        this.scheduleConfig = plugin.getScheduleConfig();
        this.queueConfig = plugin.getQueueConfig();
        this.boxManager = plugin.getBoxManager();
        this.scheduleManager = plugin.getScheduleManager();
        registerCommand(new SpawnCommand(plugin));
    }

    @Override
    protected void reload(RSCommandData data) {
        scheduleManager.stop();
        lootConfig.reload();
        boxConfig.reload();
        profileConfig.reload();
        scheduleConfig.reload();
        queueConfig.reload();
        scheduleManager.start();
    }
}

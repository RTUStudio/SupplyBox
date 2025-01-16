package kr.rtuserver.supplybox.commands;

import kr.rtuserver.framework.bukkit.api.command.RSCommand;
import kr.rtuserver.framework.bukkit.api.command.RSCommandData;
import kr.rtuserver.framework.bukkit.api.utility.player.PlayerChat;
import kr.rtuserver.supplybox.RSSupplyBox;
import kr.rtuserver.supplybox.box.Box;
import kr.rtuserver.supplybox.box.BoxManager;
import kr.rtuserver.supplybox.configuration.*;
import kr.rtuserver.supplybox.profile.Profile;
import kr.rtuserver.supplybox.schedule.ScheduleManager;

import java.util.ArrayList;
import java.util.List;

public class MainCommand extends RSCommand<RSSupplyBox> {

    private final LootConfig lootConfig;
    private final BoxConfig boxConfig;
    private final ProfileConfig profileConfig;
    private final ScheduleConfig scheduleConfig;
    private final QueueConfig queueConfig;

    private final BoxManager boxManager;
    private final ScheduleManager scheduleManager;

    public MainCommand(RSSupplyBox plugin) {
        super(plugin, "rssb");
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

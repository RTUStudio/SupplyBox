package com.github.ipecter.rtustudio.supplybox;

import com.github.ipecter.rtustudio.supplybox.box.BoxManager;
import com.github.ipecter.rtustudio.supplybox.command.MainCommand;
import com.github.ipecter.rtustudio.supplybox.configuration.*;
import com.github.ipecter.rtustudio.supplybox.hook.Hooks;
import com.github.ipecter.rtustudio.supplybox.listener.BoxInteractEvent;
import com.github.ipecter.rtustudio.supplybox.listener.ChunkLoadEvent;
import com.github.ipecter.rtustudio.supplybox.listener.ItemInteractEvent;
import com.github.ipecter.rtustudio.supplybox.loot.LootManager;
import com.github.ipecter.rtustudio.supplybox.schedule.ScheduleManager;
import kr.rtuserver.framework.bukkit.api.RSPlugin;
import lombok.Getter;

@Getter
public class SupplyBox extends RSPlugin {

    private LootConfig lootConfig;
    private BoxConfig boxConfig;
    private ProfileConfig profileConfig;
    private ScheduleConfig scheduleConfig;

    private DiscordConfig discordConfig;
    private QueueConfig queueConfig;

    private BoxManager boxManager;
    private LootManager lootManager;
    private ScheduleManager scheduleManager;

    private Hooks hooks;

    @Override
    protected void enable() {

        lootConfig = new LootConfig(this);
        boxConfig = new BoxConfig(this);
        profileConfig = new ProfileConfig(this);
        scheduleConfig = new ScheduleConfig(this);

        discordConfig = new DiscordConfig(this);
        queueConfig = new QueueConfig(this);

        boxManager = new BoxManager(this);
        lootManager = new LootManager(this);
        scheduleManager = new ScheduleManager(this);

        hooks = new Hooks(this);

        registerEvent(new ChunkLoadEvent(this));
        registerEvent(new BoxInteractEvent(this));
        registerEvent(new ItemInteractEvent(this));

        registerCommand(new MainCommand(this), true);

        scheduleManager.start();
    }

    @Override
    public void disable() {
        scheduleManager.stop();
    }

}

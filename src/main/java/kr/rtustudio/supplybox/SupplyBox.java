package kr.rtustudio.supplybox;

import kr.rtustudio.framework.bukkit.api.RSPlugin;
import kr.rtustudio.supplybox.box.BoxManager;
import kr.rtustudio.supplybox.command.MainCommand;
import kr.rtustudio.supplybox.configuration.BoxConfig;
import kr.rtustudio.supplybox.configuration.DiscordConfig;
import kr.rtustudio.supplybox.configuration.ProfileConfig;
import kr.rtustudio.supplybox.configuration.QueueConfig;
import kr.rtustudio.supplybox.configuration.ScheduleConfig;
import kr.rtustudio.supplybox.hook.Discord;
import kr.rtustudio.supplybox.listener.BoxInteractEvent;
import kr.rtustudio.supplybox.listener.ChunkLoadEvent;
import kr.rtustudio.supplybox.listener.ItemInteractEvent;
import kr.rtustudio.supplybox.loot.LootManager;
import kr.rtustudio.supplybox.schedule.ScheduleManager;
import lombok.Getter;

@Getter
public class SupplyBox extends RSPlugin {

    private BoxConfig boxConfig;
    private ProfileConfig profileConfig;
    private QueueConfig queueConfig;

    private BoxManager boxManager;
    private LootManager lootManager;
    private ScheduleManager scheduleManager;

    private Discord discord;

    @Override
    protected void enable() {
        initStorage("Queue");

        registerConfiguration(DiscordConfig.class, "Discord");
        registerConfiguration(ScheduleConfig.class, "Schedule");

        boxConfig = new BoxConfig(this);
        profileConfig = new ProfileConfig(this);
        queueConfig = new QueueConfig(this);

        boxManager = new BoxManager(this);
        lootManager = new LootManager(this);
        scheduleManager = new ScheduleManager(this);

        discord = new Discord(this);

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

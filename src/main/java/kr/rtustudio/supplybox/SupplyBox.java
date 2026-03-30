package kr.rtustudio.supplybox;

import kr.rtustudio.configurate.model.ConfigList;
import kr.rtustudio.configurate.model.ConfigPath;
import kr.rtustudio.framework.bukkit.api.RSPlugin;
import kr.rtustudio.supplybox.box.BoxManager;
import kr.rtustudio.supplybox.command.MainCommand;
import kr.rtustudio.supplybox.configuration.*;
import kr.rtustudio.supplybox.handler.BoxInteract;
import kr.rtustudio.supplybox.handler.ChunkLoad;
import kr.rtustudio.supplybox.handler.ItemInteract;
import kr.rtustudio.supplybox.hook.Discord;
import kr.rtustudio.supplybox.loot.LootManager;
import kr.rtustudio.supplybox.schedule.ScheduleManager;
import lombok.Getter;

@Getter
public class SupplyBox extends RSPlugin {

    @Getter
    private static SupplyBox instance;

    private QueueConfig queueConfig;

    private BoxManager boxManager;
    private LootManager lootManager;
    private ScheduleManager scheduleManager;

    private Discord discord;

    @Override
    protected void load() {
        instance = this;
    }

    @Override
    protected void enable() {
        registerStorage("Queue");

        registerConfiguration(DiscordConfig.class, ConfigPath.of("Discord"));
        registerConfiguration(ScheduleConfig.class, ConfigPath.of("Schedule"));

        registerConfigurations(LootConfig.class, ConfigPath.of("Loots"));
        registerConfigurations(BoxConfig.class, ConfigPath.of("Boxes"));
        registerConfigurations(ProfileConfig.class, ConfigPath.of("Profiles"));

        queueConfig = new QueueConfig(this);

        boxManager = new BoxManager(this);
        boxManager.loadSchedules();
        lootManager = new LootManager(this);
        scheduleManager = new ScheduleManager(this);

        discord = new Discord(this);

        registerEvent(new ChunkLoad(this));
        registerEvent(new BoxInteract(this));
        registerEvent(new ItemInteract(this));

        registerCommand(new MainCommand(this), true);

        scheduleManager.start();
    }

    @Override
    protected void disable() {
        scheduleManager.stop();
    }

    public ConfigList<LootConfig> getLoots() {
        return getConfigurations(LootConfig.class);
    }

    public ConfigList<BoxConfig> getBoxes() {
        return getConfigurations(BoxConfig.class);
    }

    public ConfigList<ProfileConfig> getProfiles() {
        return getConfigurations(ProfileConfig.class);
    }
}

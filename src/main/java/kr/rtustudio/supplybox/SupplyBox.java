package kr.rtustudio.supplybox;

import kr.rtustudio.configurate.model.ConfigList;
import kr.rtustudio.configurate.model.ConfigPath;
import kr.rtustudio.framework.bukkit.api.RSPlugin;
import kr.rtustudio.supplybox.box.BoxManager;
import kr.rtustudio.supplybox.command.MainCommand;
import kr.rtustudio.supplybox.configuration.BoxConfig;
import kr.rtustudio.supplybox.configuration.DiscordConfig;
import kr.rtustudio.supplybox.configuration.LootConfig;
import kr.rtustudio.supplybox.configuration.ProfileConfig;
import kr.rtustudio.supplybox.configuration.QueueConfig;
import kr.rtustudio.supplybox.configuration.ScheduleConfig;
import kr.rtustudio.supplybox.hook.Discord;
import kr.rtustudio.supplybox.handler.BoxInteractEvent;
import kr.rtustudio.supplybox.handler.ChunkLoadEvent;
import kr.rtustudio.supplybox.handler.ItemInteractEvent;
import kr.rtustudio.supplybox.loot.LootManager;
import kr.rtustudio.supplybox.schedule.ScheduleManager;
import lombok.Getter;

@Getter
public class SupplyBox extends RSPlugin {

    private QueueConfig queueConfig;

    private BoxManager boxManager;
    private LootManager lootManager;
    private ScheduleManager scheduleManager;

    private Discord discord;

    @Override
    protected void enable() {
        registerStorage("Queue");

        registerConfiguration(DiscordConfig.class, ConfigPath.of("Discord"));
        registerConfiguration(ScheduleConfig.class, ConfigPath.of("Schedule"));

        registerConfigurations(LootConfig.class, ConfigPath.of("Config", "Loots"));
        registerConfigurations(BoxConfig.class, ConfigPath.of("Config", "Boxes"));
        registerConfigurations(ProfileConfig.class, ConfigPath.of("Config", "Profiles"));

        queueConfig = new QueueConfig(this);

        boxManager = new BoxManager(this);
        boxManager.loadScheduleBoxes();
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

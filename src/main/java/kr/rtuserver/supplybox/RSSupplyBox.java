package kr.rtuserver.supplybox;

import kr.rtuserver.framework.bukkit.api.RSPlugin;
import kr.rtuserver.supplybox.box.BoxManager;
import kr.rtuserver.supplybox.commands.Command;
import kr.rtuserver.supplybox.configuration.*;
import kr.rtuserver.supplybox.listeners.BoxInteractEvent;
import kr.rtuserver.supplybox.listeners.ChunkLoadEvent;
import kr.rtuserver.supplybox.listeners.ItemInteractEvent;
import kr.rtuserver.supplybox.loot.LootManager;
import kr.rtuserver.supplybox.schedule.ScheduleManager;
import lombok.Getter;

@Getter
public class RSSupplyBox extends RSPlugin {

    private LootConfig lootConfig;
    private BoxConfig boxConfig;
    private ProfileConfig profileConfig;
    private ScheduleConfig scheduleConfig;
    private QueueConfig queueConfig;

    private BoxManager boxManager;
    private LootManager lootManager;
    private ScheduleManager scheduleManager;

    @Override
    protected void enable() {

        lootConfig = new LootConfig(this);
        boxConfig = new BoxConfig(this);
        profileConfig = new ProfileConfig(this);
        scheduleConfig = new ScheduleConfig(this);
        queueConfig = new QueueConfig(this);

        boxManager = new BoxManager(this);
        lootManager = new LootManager(this);
        scheduleManager = new ScheduleManager(this);

        registerEvent(new ChunkLoadEvent(this));
        registerEvent(new BoxInteractEvent(this));
        registerEvent(new ItemInteractEvent(this));

        registerCommand(new Command(this));

        scheduleManager.start();
    }

    @Override
    public void disable() {
        scheduleManager.stop();
    }

}

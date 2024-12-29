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

public class Command extends RSCommand<RSSupplyBox> {

    private final LootConfig lootConfig;
    private final BoxConfig boxConfig;
    private final ProfileConfig profileConfig;
    private final ScheduleConfig scheduleConfig;
    private final QueueConfig queueConfig;

    private final BoxManager boxManager;
    private final ScheduleManager scheduleManager;

    public Command(RSSupplyBox plugin) {
        super(plugin, "rssb", true);
        this.lootConfig = plugin.getLootConfig();
        this.boxConfig = plugin.getBoxConfig();
        this.profileConfig = plugin.getProfileConfig();
        this.scheduleConfig = plugin.getScheduleConfig();
        this.queueConfig = plugin.getQueueConfig();
        this.boxManager = plugin.getBoxManager();
        this.scheduleManager = plugin.getScheduleManager();
    }

    @Override
    protected boolean execute(RSCommandData data) {
        PlayerChat chat = PlayerChat.of(getPlugin());
        if (data.equals(0, getCommand().get(getPlayer(), "spawn"))) {
            Box box = boxConfig.get(data.args(1));
            if (box != null) {
                Profile profile = profileConfig.get(data.args(2));
                if (profile != null) {
                    boxManager.spawn(box, profile);
                } else chat.announce(getSender(), getMessage().get(getPlayer(), "notFound.profile"));
            } else chat.announce(getSender(), getMessage().get(getPlayer(), "notFound.box"));
            return true;
        }
        return false;
    }

    @Override
    protected void wrongUsage(RSCommandData data) {
        PlayerChat chat = PlayerChat.of(getPlugin());
        chat.send(getSender(), String.format("<gray> - </gray>/%s %s", getName(), getCommand().get(getPlayer(), "spawn")) + " [box] [profile]");
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

    @Override
    protected List<String> tabComplete(RSCommandData data) {
        List<String> list = new ArrayList<>();
        if (data.length(1)) {
            list.add(getCommand().get(getPlayer(), "spawn"));
        }
        if (data.length(2)) {
            if (data.equals(0, getCommand().get(getPlayer(), "spawn"))) {
                list.addAll(boxConfig.getMap().keySet());
            }
        }
        if (data.length(3)) {
            if (data.equals(0, getCommand().get(getPlayer(), "spawn"))) {
                list.addAll(profileConfig.getMap().keySet());
            }
        }
        return list;
    }
}

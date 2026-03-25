package kr.rtustudio.supplybox.command;

import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.configuration.ScheduleConfig;
import kr.rtustudio.framework.bukkit.api.command.RSCommand;
import kr.rtustudio.framework.bukkit.api.command.CommandArgs;

import java.util.Map;

public class DebugCommand extends RSCommand<SupplyBox> {

    public DebugCommand(SupplyBox plugin) {
        super(plugin, "sbdebug");
    }

    @Override
    protected Result execute(CommandArgs data) {
        ScheduleConfig sc = plugin.getConfiguration(ScheduleConfig.class);
        for (Map.Entry<String, ScheduleConfig.Entry> entry : sc.getSchedules().entrySet()) {
            System.out.println("Schedule: " + entry.getKey() + " -> Box: " + entry.getValue().getBox() + ", Profile: " + entry.getValue().getProfile() + ", isDelay: " + entry.getValue().isDelayPerChest());
        }
        return Result.SUCCESS;
    }
}

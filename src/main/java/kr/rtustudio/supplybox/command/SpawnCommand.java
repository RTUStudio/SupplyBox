package kr.rtustudio.supplybox.command;

import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.box.BoxManager;
import kr.rtustudio.supplybox.configuration.BoxConfig;
import kr.rtustudio.supplybox.configuration.ProfileConfig;
import kr.rtustudio.framework.bukkit.api.command.RSCommand;
import kr.rtustudio.framework.bukkit.api.command.CommandArgs;
import org.bukkit.entity.Player;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;

public class SpawnCommand extends RSCommand<SupplyBox> {

    private final BoxManager boxManager;

    public SpawnCommand(SupplyBox plugin) {
        super(plugin, "spawn");
        this.boxManager = plugin.getBoxManager();
    }

    @Override
    protected Result execute(CommandArgs data) {
        if (data.isEmpty()) return Result.WRONG_USAGE;
        String boxName = data.get(1);
        BoxConfig box = plugin.getBoxes().get(boxName);
        if (box == null) {
            notifier.announce(getAudience(), message.get((Player) getSender(), "notFound.box"));
            return Result.FAILURE;
        }
        String profileName = data.get(2);
        ProfileConfig profile = plugin.getProfiles().get(profileName);
        if (profile == null) {
            notifier.announce(getAudience(), message.get((Player) getSender(), "notFound.profile"));
            return Result.FAILURE;
        }
        boxManager.spawn(boxName, box, profile);
        return Result.SUCCESS;
    }

    @Override
    public List<String> tabComplete(CommandArgs data) {
        List<String> list = new ObjectArrayList<>();
        if (data.length(2)) {
            list.addAll(plugin.getBoxes().keys());
        }
        if (data.length(3)) {
            list.addAll(plugin.getProfiles().keys());
        }
        return list;
    }

}

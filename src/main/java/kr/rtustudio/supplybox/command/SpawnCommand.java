package kr.rtustudio.supplybox.command;

import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.box.Box;
import kr.rtustudio.supplybox.box.BoxManager;
import kr.rtustudio.supplybox.configuration.BoxConfig;
import kr.rtustudio.supplybox.configuration.ProfileConfig;
import kr.rtustudio.supplybox.profile.Profile;
import kr.rtustudio.framework.bukkit.api.command.RSCommand;
import kr.rtustudio.framework.bukkit.api.command.RSCommandData;

import java.util.ArrayList;
import java.util.List;

public class SpawnCommand extends RSCommand<SupplyBox> {

    private final BoxConfig boxConfig;
    private final ProfileConfig profileConfig;

    private final BoxManager boxManager;

    public SpawnCommand(SupplyBox plugin) {
        super(plugin, "spawn");
        this.boxConfig = plugin.getBoxConfig();
        this.profileConfig = plugin.getProfileConfig();
        this.boxManager = plugin.getBoxManager();
    }

    @Override
    protected Result execute(RSCommandData data) {
        if (data.isEmpty()) return Result.WRONG_USAGE;
        Box box = boxConfig.get(data.args(1));
        if (box == null) {
            chat().announce(audience(), message().get(player(), "notFound.box"));
            return Result.FAILURE;
        }
        Profile profile = profileConfig.get(data.args(2));
        if (profile == null) {
            chat().announce(audience(), message().get(player(), "notFound.profile"));
            return Result.FAILURE;
        }
        boxManager.spawn(box, profile);
        return Result.SUCCESS;
    }

    @Override
    public List<String> tabComplete(RSCommandData data) {
        List<String> list = new ArrayList<>();
        if (data.length(2)) {
            list.addAll(boxConfig.getMap().keySet());
        }
        if (data.length(3)) {
            list.addAll(profileConfig.getMap().keySet());
        }
        return list;
    }

}

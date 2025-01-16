package kr.rtuserver.supplybox.commands;

import kr.rtuserver.framework.bukkit.api.command.RSCommand;
import kr.rtuserver.framework.bukkit.api.command.RSCommandData;
import kr.rtuserver.supplybox.RSSupplyBox;
import kr.rtuserver.supplybox.box.Box;
import kr.rtuserver.supplybox.box.BoxManager;
import kr.rtuserver.supplybox.configuration.BoxConfig;
import kr.rtuserver.supplybox.configuration.ProfileConfig;
import kr.rtuserver.supplybox.profile.Profile;

import java.util.ArrayList;
import java.util.List;

public class SpawnCommand extends RSCommand<RSSupplyBox> {

    private final BoxConfig boxConfig;
    private final ProfileConfig profileConfig;

    private final BoxManager boxManager;

    public SpawnCommand(RSSupplyBox plugin) {
        super(plugin, "spawn");
        this.boxConfig = plugin.getBoxConfig();
        this.profileConfig = plugin.getProfileConfig();
        this.boxManager = plugin.getBoxManager();
    }

    @Override
    protected boolean execute(RSCommandData data) {
        Box box = boxConfig.get(data.args(1));
        if (box != null) {
            Profile profile = profileConfig.get(data.args(2));
            if (profile != null) {
                boxManager.spawn(box, profile);
            } else getChat().announce(getSender(), getMessage().get(getPlayer(), "notFound.profile"));
        } else getChat().announce(getSender(), getMessage().get(getPlayer(), "notFound.box"));
        return true;
    }

    @Override
    protected List<String> tabComplete(RSCommandData data) {
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

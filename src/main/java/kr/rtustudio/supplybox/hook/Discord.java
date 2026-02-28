package kr.rtustudio.supplybox.hook;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.configuration.DiscordConfig;

public class Discord {

    private final DiscordSRV api;
    private final boolean hooked;
    private final SupplyBox plugin;

    public Discord(SupplyBox plugin) {
        this.plugin = plugin;
        this.hooked = plugin.getFramework().isEnabledDependency("DiscordSRV");
        this.api = hooked ? DiscordSRV.getPlugin() : null;
    }

    public void sendMessage(String message) {
        if (!hooked) return;
        DiscordConfig config = plugin.getConfiguration(DiscordConfig.class);
        if (config == null || config.getChannel().isEmpty()) return;
        TextChannel channel = api.getMainGuild().getTextChannelById(config.getChannel());
        DiscordUtil.sendMessage(channel, message);
    }

}

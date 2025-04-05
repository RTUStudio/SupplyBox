package com.github.ipecter.rtustudio.supplybox.hook;

import com.github.ipecter.rtustudio.supplybox.SupplyBox;
import com.github.ipecter.rtustudio.supplybox.configuration.DiscordConfig;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;

public class Discord {

    private final DiscordSRV api;
    private final boolean hooked;
    private final DiscordConfig config;

    public Discord(SupplyBox plugin) {
        this.hooked = plugin.getFramework().isEnabledDependency("DiscordSRV");
        if (hooked) {
            api = DiscordSRV.getPlugin();
        } else api = null;
        this.config = plugin.getDiscordConfig();
    }

    public void sendMessage(String message) {
        if (!hooked) return;
        TextChannel channel = api.getMainGuild().getTextChannelById(config.getChannel());
        DiscordUtil.sendMessage(channel, message);
    }

}

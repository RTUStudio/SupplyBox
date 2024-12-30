package kr.rtuserver.supplybox.configuration;

import kr.rtuserver.framework.bukkit.api.config.RSConfiguration;
import kr.rtuserver.supplybox.RSSupplyBox;
import lombok.Getter;

@Getter
public class DiscordConfig extends RSConfiguration<RSSupplyBox> {

    private String channel = "";
    private String message = "";

    public DiscordConfig(RSSupplyBox plugin) {
        super(plugin, "Discord.yml", null);
        setup(this);
    }

    private void init() {
        getConfig().setComment("", """
                DiscordSRV plugin is required
                DiscordSRV 플러그인이 필요합니다""");
        channel = getString("channel", channel, """
                Channel to send supply box spawn notifications
                보급 상자 생성 알림을 전송할 채널""");
        message = getString("message", message, """
                Notifications message
                알림 메세지""");
    }
}

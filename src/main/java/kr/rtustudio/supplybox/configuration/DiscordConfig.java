package kr.rtustudio.supplybox.configuration;

import kr.rtustudio.configurate.objectmapping.meta.Comment;
import kr.rtustudio.framework.bukkit.api.configuration.ConfigurationPart;
import lombok.Getter;

@Getter
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class DiscordConfig extends ConfigurationPart {

    @Comment("""
            DiscordSRV plugin is required
            DiscordSRV 플러그인이 필요합니다""")
    private String channel = "0000000000";

    @Comment("""
            Notifications message
            알림 메세지""")
    private String message = "Random box [display] has been spawned at [world] [x],[z]";
}

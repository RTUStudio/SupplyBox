package kr.rtustudio.supplybox.hook;

import kr.rtustudio.supplybox.SupplyBox;
import lombok.Getter;

/**
 * @deprecated Use {@link SupplyBox#getDiscord()} directly.
 */
@Deprecated
@Getter
public class Hooks {

    private final Discord discord;

    public Hooks(SupplyBox plugin) {
        this.discord = plugin.getDiscord();
    }

}

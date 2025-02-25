package com.github.ipecter.rtustudio.supplybox.hook;

import com.github.ipecter.rtustudio.supplybox.SupplyBox;
import lombok.Getter;

@Getter
public class Hooks {

    private final Discord discord;

    public Hooks(SupplyBox plugin) {
        this.discord = new Discord(plugin);
    }

}

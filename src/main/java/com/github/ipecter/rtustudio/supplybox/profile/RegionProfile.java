package com.github.ipecter.rtustudio.supplybox.profile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class RegionProfile extends Profile {

    private String world = "world";
    private int centerX = 0;
    private int centerZ = 0;
    private int radius = 100;
    private Type type = Type.SQUARE;
    private int amount = 1;

    public RegionProfile(String name) {
        super(name);
    }

    public enum Type {
        SQUARE,
        CIRCLE
    }

}

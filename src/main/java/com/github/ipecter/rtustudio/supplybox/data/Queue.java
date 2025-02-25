package com.github.ipecter.rtustudio.supplybox.data;

import com.github.ipecter.rtustudio.supplybox.box.Box;
import org.bukkit.Location;

public record Queue(Location location, Box box) {
}

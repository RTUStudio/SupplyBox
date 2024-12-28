package kr.rtuserver.supplybox.data;

import kr.rtuserver.supplybox.box.Box;
import org.bukkit.Location;

public record Queue(Location location, Box box) {
}

package kr.rtuserver.supplybox.schedule;

import kr.rtuserver.supplybox.box.Box;
import kr.rtuserver.supplybox.profile.Profile;

public record Schedule(String name, boolean enable, int period, Box box, Profile profile) {
}

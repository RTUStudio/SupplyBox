package com.github.ipecter.rtustudio.supplybox.schedule;

import com.github.ipecter.rtustudio.supplybox.box.Box;
import com.github.ipecter.rtustudio.supplybox.profile.Profile;

public record Schedule(String name, boolean enable, int period, Box box, Profile profile) {
}

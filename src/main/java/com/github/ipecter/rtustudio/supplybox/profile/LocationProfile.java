package com.github.ipecter.rtustudio.supplybox.profile;

import com.github.ipecter.rtustudio.supplybox.data.WorldCoordinate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
public class LocationProfile extends Profile {

    private int select = 1;
    private List<WorldCoordinate> locations = new ArrayList<>();

    public LocationProfile(String name) {
        super(name);
    }

}

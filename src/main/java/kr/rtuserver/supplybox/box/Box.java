package kr.rtuserver.supplybox.box;

import kr.rtuserver.supplybox.loot.Loot;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Box {

    private final String name;

    private String display;

    private boolean alertMinecraft = true;
    private boolean alertDiscord = true;

    private String block = "chest";

    private String itemKey = "";
    private String itemBox = "";

    private Interact interact = Interact.DROP;

    private Loot loot;

    public enum Interact {
        DROP,
        INVENTORY,
        GIVE
    }

}

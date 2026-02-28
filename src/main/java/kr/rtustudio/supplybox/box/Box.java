package kr.rtustudio.supplybox.box;

import kr.rtustudio.supplybox.loot.Loot;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Box {

    private final String name;

    private boolean enabled = false;

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

package kr.rtustudio.supplybox.configuration;

import kr.rtustudio.configurate.objectmapping.meta.Comment;
import kr.rtustudio.configurate.model.ConfigurationPart;
import lombok.Getter;

import java.util.List;

@Getter
@SuppressWarnings({"unused", "CanBeFinal", "FieldCanBeLocal", "FieldMayBeFinal", "InnerClassMayBeStatic"})
public class BoxConfig extends ConfigurationPart {

    @Comment("""
            Whether this box is enabled
            이 보급 상자를 활성화할지 여부""")
    private boolean enabled = false;

    @Comment("""
            Display name of the box
            보급 상자의 표시 이름""")
    private String displayName = "";

    private Alert alert;

    @Comment("""
            Block type for the supply box
            보급 상자 블록 타입""")
    private String block = "chest";

    private Item item;

    @Comment("""
            Interaction method: DROP, INVENTORY, or GIVE
            상호작용 방법""")
    private String interact = "DROP";

    @Comment("""
            Loot table name to use
            사용할 전리품 테이블 이름""")
    private String loot = "";

    public boolean isAlertMinecraft() {
        return alert == null || alert.minecraft;
    }

    public boolean isAlertDiscord() {
        return alert == null || alert.discord;
    }

    public String getItemBox() {
        return item != null ? item.box : "";
    }

    public String getItemKey() {
        return item != null ? item.key : "";
    }

    public Interact getInteractType() {
        String normalized = interact.toUpperCase();
        if (!List.of("DROP", "INVENTORY", "GIVE").contains(normalized)) return Interact.DROP;
        return Interact.valueOf(normalized);
    }

    public enum Interact {
        DROP,
        INVENTORY,
        GIVE
    }

    public class Alert extends ConfigurationPart {
        private boolean minecraft = true;
        private boolean discord = true;
    }

    public class Item extends ConfigurationPart {
        private String box = "";
        private String key = "";
    }
}

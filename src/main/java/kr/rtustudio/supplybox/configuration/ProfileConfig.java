package kr.rtustudio.supplybox.configuration;

import kr.rtustudio.configurate.objectmapping.ConfigSerializable;
import kr.rtustudio.configurate.objectmapping.meta.Comment;
import kr.rtustudio.configurate.model.ConfigurationPart;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@SuppressWarnings({"unused", "CanBeFinal", "FieldCanBeLocal", "FieldMayBeFinal", "InnerClassMayBeStatic"})
public class ProfileConfig extends ConfigurationPart {

    private Location location;
    private Region region;

    public boolean isLocation() {
        return location != null;
    }

    public boolean isRegion() {
        return region != null;
    }

    public class Location extends ConfigurationPart {
        @Getter
        @Comment("""
                Number of locations to select
                선택할 위치의 수""")
        private int select = 1;
        @Getter
        @Comment("""
                List of world,x,y,z values
                world,x,y,z 형식의 목록""")
        private List<String> list = List.of();
    }

    public class Region extends ConfigurationPart {
        @Getter
        @Comment("""
                World name
                월드 이름""")
        private String world = "";
        @Getter
        @Comment("""
                Center X coordinate
                중심 X 좌표""")
        private int centerX = 0;
        @Getter
        @Comment("""
                Center Z coordinate
                중심 Z 좌표""")
        private int centerZ = 0;
        @Getter
        @Comment("""
                Radius of the region
                영역 반지름""")
        private int radius = 100;
        @Getter
        @Comment("""
                Region shape: SQUARE or CIRCLE
                영역 형태""")
        private String type = "SQUARE";
        @Getter
        @Comment("""
                Number of points to generate per spawn
                스폰 시 생성할 지점 수""")
        private int amount = 1;

        public Type getRegionType() {
            String normalized = type.toUpperCase();
            if (!List.of("SQUARE", "CIRCLE").contains(normalized)) return Type.SQUARE;
            return Type.valueOf(normalized);
        }

        public enum Type {
            SQUARE,
            CIRCLE
        }
    }
}

package kr.rtustudio.supplybox.configuration;

import kr.rtustudio.configurate.objectmapping.ConfigSerializable;
import kr.rtustudio.configurate.objectmapping.meta.Comment;
import kr.rtustudio.configurate.objectmapping.meta.Setting;
import kr.rtustudio.framework.bukkit.api.configuration.ConfigurationPart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class ScheduleConfig extends ConfigurationPart {

    @Comment("""
            Schedule list
            스케줄 목록. 키=스케줄 이름""")
    @Setting(nodeFromParent = true)
    private Map<String, Entry> schedules = new LinkedHashMap<>(Map.of(
            "Example", new Entry(
                    false,
                    60,
                    "Example",
                    "Example_Region",
                    false
            )
    ));

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ConfigSerializable
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    public static class Entry {
        @Comment("""
                Whether the schedule is enabled
                스케줄 활성화 여부""")
        private boolean enabled = false;
        @Comment("""
                Execution period (seconds)
                실행 주기(초)""")
        private int period = 60;
        @Comment("""
                Supply box name to use
                사용할 보급 상자 이름""")
        private String box = "";
        @Comment("""
                Profile name to use
                사용할 프로필 이름""")
        private String profile = "";
        @Comment("""
                Whether to delay respawn per chest (true: delay per chest, false: fixed period)
                상자별로 스폰 지연을 적용할지 여부 (true: 상자별 지연, false: 고정 주기)""")
        private boolean delayPerChest = false;
    }
}

package kr.rtustudio.supplybox.configuration;

import kr.rtustudio.configurate.objectmapping.ConfigSerializable;
import kr.rtustudio.configurate.objectmapping.meta.Comment;
import kr.rtustudio.configurate.model.ConfigurationPart;
import lombok.Getter;
import lombok.NoArgsConstructor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@SuppressWarnings({"unused", "CanBeFinal", "FieldCanBeLocal", "FieldMayBeFinal", "InnerClassMayBeStatic"})
public class LootConfig extends ConfigurationPart {

    private Select select;
    private Map<String, Item> items = new LinkedHashMap<>();

    public int getSelectMin() {
        return select != null ? select.min : 2;
    }

    public int getSelectMax() {
        return select != null ? select.max : 2;
    }

    public List<Item> getItemList() {
        return new ObjectArrayList<>(items.values());
    }

    public int getTotalWeight() {
        int total = 0;
        for (Item item : items.values()) total += item.weight;
        return total;
    }

    @Getter
    @NoArgsConstructor
    @ConfigSerializable
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    public static class Select {
        private int min = 2;
        private int max = 2;
    }

    @Getter
    @NoArgsConstructor
    @ConfigSerializable
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    public static class Item {
        @Comment("""
                Custom item identifier
                커스텀 아이템 식별자""")
        private String item = "";
        @Comment("""
                Weighted chance (higher = more likely)
                가중치 (높을수록 확률 증가)""")
        private int weight = 100;
        @Comment("""
                Minimum amount
                최소 수량""")
        private int min = 1;
        @Comment("""
                Maximum amount
                최대 수량""")
        private int max = 64;
    }
}

package kr.rtustudio.supplybox.loot;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import kr.rtustudio.framework.bukkit.api.registry.CustomItems;
import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.configuration.LootConfig;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class LootManager {

    private final SupplyBox plugin;

    public List<ItemStack> getItems(LootConfig loot) {
        List<LootConfig.Item> items = loot.toList();
        if (items.isEmpty()) return new ObjectArrayList<>();
        int select = ThreadLocalRandom.current().nextInt(loot.getSelectMax() - loot.getSelectMin() + 1) + loot.getSelectMin();
        List<ItemStack> result = new ObjectArrayList<>();
        for (int i = 0; i < select; i++) {
            ItemStack itemStack = getItem(items);
            if (itemStack == null) continue;
            result.add(itemStack);
        }
        return result;
    }

    public ItemStack getItem(List<LootConfig.Item> list) {
        if (list.isEmpty()) return null;
        int totalWeight = 0;
        NavigableMap<Integer, LootConfig.Item> map = new TreeMap<>();
        for (LootConfig.Item item : list) {
            totalWeight += item.getWeight();
            map.put(totalWeight, item);
        }
        if (totalWeight <= 0) return null;

        int rand = ThreadLocalRandom.current().nextInt(totalWeight) + 1;
        LootConfig.Item item = map.ceilingEntry(rand).getValue();

        ItemStack itemStack = CustomItems.from(item.getItem());
        if (itemStack == null) return null;
        int amount = ThreadLocalRandom.current().nextInt(item.getMax() - item.getMin() + 1) + item.getMin();
        itemStack.setAmount(amount);
        return itemStack;
    }

}

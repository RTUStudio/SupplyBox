package kr.rtustudio.supplybox.loot;

import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.configuration.LootConfig;
import kr.rtustudio.framework.bukkit.api.registry.CustomItems;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.*;

@RequiredArgsConstructor
public class LootManager {

    private final SupplyBox plugin;
    private final Random random = new Random();

    public List<ItemStack> getItems(LootConfig loot) {
        List<LootConfig.Item> items = loot.getItemList();
        if (items.isEmpty()) return new ObjectArrayList<>();
        int select = random.nextInt(loot.getSelectMax() - loot.getSelectMin() + 1) + loot.getSelectMin();
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

        int rand = random.nextInt(totalWeight) + 1;
        LootConfig.Item item = map.ceilingEntry(rand).getValue();

        ItemStack itemStack = CustomItems.from(item.getItem());
        if (itemStack == null) return null;
        int amount = random.nextInt(item.getMax() - item.getMin() + 1) + item.getMin();
        itemStack.setAmount(amount);
        return itemStack;
    }

}

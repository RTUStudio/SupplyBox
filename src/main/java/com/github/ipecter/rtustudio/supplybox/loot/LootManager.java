package com.github.ipecter.rtustudio.supplybox.loot;

import com.github.ipecter.rtustudio.supplybox.SupplyBox;
import kr.rtuserver.framework.bukkit.api.registry.CustomItems;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@RequiredArgsConstructor
public class LootManager {

    private final SupplyBox plugin;
    private final Random random = new Random();

    public List<ItemStack> getItems(Loot loot) {
        List<ItemStack> items = new ArrayList<>();
        int select = random.nextInt(loot.getSelectMax() - loot.getSelectMin() + 1) + loot.getSelectMin();
        for (int i = 0; i < select; i++) {
            ItemStack itemStack = getItem(loot.getList());
            if (itemStack == null) continue;
            items.add(itemStack);
        }
        return items;
    }

    public ItemStack getItem(List<Loot.Item> list) {
        int totalWeight = 0;
        NavigableMap<Integer, Loot.Item> map = new TreeMap<>();
        for (Loot.Item item : list) {
            totalWeight += item.getWeight();
            map.put(totalWeight, item);
        }

        int rand = random.nextInt(totalWeight) + 1;
        Loot.Item item = map.ceilingEntry(rand).getValue();

        ItemStack itemStack = CustomItems.from(item.getItem());
        if (itemStack == null) return null;
        int amount = random.nextInt(item.getMax() - item.getMin() + 1) + item.getMin();
        itemStack.setAmount(amount);
        return itemStack;
    }

}

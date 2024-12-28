package kr.rtuserver.supplybox.loot;

import kr.rtuserver.framework.bukkit.api.utility.compatible.ItemCompat;
import kr.rtuserver.supplybox.RSSupplyBox;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public class LootManager {

    private final RSSupplyBox plugin;
    private final Random random = new Random();

    public List<ItemStack> getItems(Loot loot) {
        List<ItemStack> items = new ArrayList<>();
        int select = random.nextInt(loot.getSelectMax() - loot.getSelectMin() + 1) + loot.getSelectMin();
        for (int i = 0; i < select; i++) {
            for (int j = 0; j < Math.min(54, loot.getList().size()); j++) {
                Loot.Item item = loot.getList().get(j);
                int chance = random.nextInt(loot.getChance());
                if (chance < item.getChance()) {
                    ItemStack itemStack = getItem(item);
                    if (itemStack == null) continue;
                    items.add(itemStack);
                }
            }
        }
        return items;
    }

    private ItemStack getItem(Loot.Item item) {
        ItemStack itemStack = ItemCompat.from(item.getItem());
        if (itemStack == null) return null;
        int amount = random.nextInt(item.getMax() - item.getMin() + 1) + item.getMin();
        itemStack.setAmount(amount);
        return itemStack;
    }

}

package kr.rtustudio.supplybox.box;

import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.configuration.BoxConfig;
import kr.rtustudio.supplybox.configuration.LootConfig;
import kr.rtustudio.framework.bukkit.api.format.ComponentFormatter;
import kr.rtustudio.framework.bukkit.api.inventory.RSInventory;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BoxInventory extends RSInventory<SupplyBox> {

    private final Inventory inventory;

    public BoxInventory(SupplyBox plugin, BoxConfig box, List<ItemStack> items) {
        super(plugin);
        LootConfig loot = plugin.getLoots().get(box.getLoot());
        int selectMax = loot != null ? loot.getSelectMax() : items.size();
        int line = (int) Math.ceil((double) selectMax / 9);
        this.inventory = createInventory(line * 9, ComponentFormatter.mini(box.getDisplayName()));
        for (ItemStack stack : items) inventory.addItem(stack);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClose(Event<InventoryCloseEvent> event) {
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null || itemStack.getType().isAir()) continue;
            event.player().getWorld().dropItemNaturally(event.player().getLocation(), itemStack);
        }
    }

}

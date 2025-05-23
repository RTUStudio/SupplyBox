package com.github.ipecter.rtustudio.supplybox.box;

import com.github.ipecter.rtustudio.supplybox.SupplyBox;
import kr.rtuserver.framework.bukkit.api.format.ComponentFormatter;
import kr.rtuserver.framework.bukkit.api.inventory.RSInventory;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BoxInventory extends RSInventory<SupplyBox> {

    private final Inventory inventory;

    public BoxInventory(SupplyBox plugin, Box box, List<ItemStack> items) {
        super(plugin);
        int line = (int) Math.ceil((double) box.getLoot().getSelectMax() / 9);
        this.inventory = createInventory(line * 9, ComponentFormatter.mini(box.getDisplay()));
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

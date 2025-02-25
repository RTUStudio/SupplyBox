package com.github.ipecter.rtustudio.supplybox.box;

import com.github.ipecter.rtustudio.supplybox.SupplyBox;
import kr.rtuserver.framework.bukkit.api.inventory.RSInventory;
import kr.rtuserver.framework.bukkit.api.utility.format.ComponentFormatter;
import kr.rtuserver.framework.bukkit.api.utility.platform.MinecraftVersion;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
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

    private Inventory createInventory(int size, Component component) {
        if (MinecraftVersion.isPaper()) return Bukkit.createInventory(this, size, component);
        else return Bukkit.createInventory(this, size, ComponentFormatter.legacy(component));
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClose(Event<InventoryCloseEvent> event, Close close) {
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null || itemStack.getType().isAir()) continue;
            event.player().getWorld().dropItemNaturally(event.player().getLocation(), itemStack);
        }
    }

}

package kr.rtuserver.supplybox.box;

import kr.rtuserver.framework.bukkit.api.RSPlugin;
import kr.rtuserver.framework.bukkit.api.inventory.RSInventory;
import kr.rtuserver.framework.bukkit.api.utility.format.ComponentFormatter;
import kr.rtuserver.framework.bukkit.api.utility.platform.MinecraftVersion;
import kr.rtuserver.supplybox.RSSupplyBox;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BoxInventory extends RSInventory<RSSupplyBox> {

    private final Inventory inventory;

    public BoxInventory(RSSupplyBox plugin, Box box, List<ItemStack> items) {
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

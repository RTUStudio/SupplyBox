package com.github.ipecter.rtustudio.supplybox.listener;

import com.github.ipecter.rtustudio.supplybox.SupplyBox;
import com.github.ipecter.rtustudio.supplybox.box.Box;
import com.github.ipecter.rtustudio.supplybox.box.BoxInventory;
import com.github.ipecter.rtustudio.supplybox.configuration.BoxConfig;
import com.github.ipecter.rtustudio.supplybox.loot.LootManager;
import kr.rtuserver.framework.bukkit.api.listener.RSListener;
import kr.rtuserver.framework.bukkit.api.registry.CustomItems;
import kr.rtuserver.framework.bukkit.api.utility.player.PlayerChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemInteractEvent extends RSListener<SupplyBox> {

    private final BoxConfig boxConfig;
    private final LootManager lootManager;

    public ItemInteractEvent(SupplyBox plugin) {
        super(plugin);
        this.boxConfig = plugin.getBoxConfig();
        this.lootManager = plugin.getLootManager();
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent e) {
        if (!e.getAction().isRightClick()) return;
        ItemStack item = e.getItem();
        if (item == null || item.getType().isAir()) return;
        Player player = e.getPlayer();
        for (Box box : boxConfig.getMap().values()) {
            ItemStack boxItem = CustomItems.from(box.getItemBox());
            if (boxItem == null) continue;
            if (CustomItems.isSimilar(e.getItem(), boxItem)) {
                e.setCancelled(true);
                if (box.getItemKey().isEmpty()) {
                    player.getInventory().remove(boxItem);
                    process(e.getPlayer(), box);
                } else {
                    ItemStack key = CustomItems.from(box.getItemKey());
                    if (player.getInventory().containsAtLeast(key, 1)) {
                        player.getInventory().removeItem(key);

                        player.getInventory().removeItem(boxItem);
                        process(e.getPlayer(), box);
                    } else {
                        PlayerChat.of(getPlugin()).announce(player, getMessage().get(player, "box.noKey"));
                    }
                    return;
                }
            }
        }
    }

    private void process(Player player, Box box) {
        List<ItemStack> items = lootManager.getItems(box.getLoot());
        switch (box.getInteract()) {
            case DROP -> {
                for (ItemStack itemStack : items) player.getWorld().dropItem(player.getLocation(), itemStack);
            }
            case INVENTORY -> {
                BoxInventory inventory = new BoxInventory(getPlugin(), box, items);
                player.openInventory(inventory.getInventory());
            }
            case GIVE -> {
                for (ItemStack itemStack : items) {
                    if (player.getInventory().firstEmpty() == -1) {
                        player.getWorld().dropItem(player.getLocation(), itemStack);
                    } else player.getInventory().addItem(itemStack);
                }
            }
        }
    }

}

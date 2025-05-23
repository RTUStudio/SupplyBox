package com.github.ipecter.rtustudio.supplybox.listener;

import com.github.ipecter.rtustudio.supplybox.SupplyBox;
import com.github.ipecter.rtustudio.supplybox.box.Box;
import com.github.ipecter.rtustudio.supplybox.box.BoxInventory;
import com.github.ipecter.rtustudio.supplybox.configuration.BoxConfig;
import com.github.ipecter.rtustudio.supplybox.loot.LootManager;
import com.jeff_media.customblockdata.CustomBlockData;
import kr.rtuserver.framework.bukkit.api.listener.RSListener;
import kr.rtuserver.framework.bukkit.api.registry.CustomBlocks;
import kr.rtuserver.framework.bukkit.api.registry.CustomItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class BoxInteractEvent extends RSListener<SupplyBox> {

    private final BoxConfig boxConfig;
    private final LootManager lootManager;

    private final NamespacedKey key;

    public BoxInteractEvent(SupplyBox plugin) {
        super(plugin);
        this.boxConfig = plugin.getBoxConfig();
        this.lootManager = plugin.getLootManager();

        this.key = new NamespacedKey(plugin, "box");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if (block == null || block.getType().isAir()) return;
        Box box = getBox(block);
        if (box == null) return;
        e.setCancelled(true);
        if (box.getItemKey().isEmpty()) {
            process(e.getPlayer(), block, box);
        } else {
            ItemStack key = CustomItems.from(box.getItemKey());
            if (player.getInventory().containsAtLeast(key, 1)) {
                player.getInventory().removeItem(key);
                process(e.getPlayer(), block, box);
            } else chat().announce(player, getMessage().get(player, "box.noKey"));
        }
    }

    private void process(Player player, Block block, Box box) {
        removeBlock(block);
        List<ItemStack> items = lootManager.getItems(box.getLoot());
        switch (box.getInteract()) {
            case DROP -> {
                Location loc = block.getLocation();
                for (ItemStack itemStack : items) loc.getWorld().dropItem(loc, itemStack);
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

    private void removeBlock(Block block) {
        PersistentDataContainer pdc = new CustomBlockData(block, getPlugin());
        if (pdc.has(key, PersistentDataType.STRING)) {
            pdc.remove(key);
        }
        block.setType(Material.AIR);
    }

    private Box getBox(Block block) {
        PersistentDataContainer pdc = new CustomBlockData(block, getPlugin());
        if (pdc.has(key, PersistentDataType.STRING)) {
            Box box = new Box(pdc.get(key, PersistentDataType.STRING));
            String target = CustomBlocks.to(block);
            String boxBlock = box.getBlock().contains(":") ? box.getBlock() : "minecraft:" + box.getBlock();
            if (boxBlock.equalsIgnoreCase(target)) {
                return boxConfig.get(pdc.get(key, PersistentDataType.STRING));
            } else pdc.remove(key);
        }
        return null;
    }

}

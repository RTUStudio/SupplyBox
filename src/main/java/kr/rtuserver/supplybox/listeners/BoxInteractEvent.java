package kr.rtuserver.supplybox.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import kr.rtuserver.framework.bukkit.api.listener.RSListener;
import kr.rtuserver.framework.bukkit.api.utility.compatible.BlockCompat;
import kr.rtuserver.supplybox.RSSupplyBox;
import kr.rtuserver.supplybox.box.Box;
import kr.rtuserver.supplybox.box.BoxInventory;
import kr.rtuserver.supplybox.configuration.BoxConfig;
import kr.rtuserver.supplybox.loot.LootManager;
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

public class BoxInteractEvent extends RSListener<RSSupplyBox> {

    private final BoxConfig boxConfig;
    private final LootManager lootManager;

    private final NamespacedKey key;

    public BoxInteractEvent(RSSupplyBox plugin) {
        super(plugin);
        this.boxConfig = plugin.getBoxConfig();
        this.lootManager = plugin.getLootManager();

        this.key = new NamespacedKey(plugin, "box");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null || block.getType().isAir()) return;
        Box box = getBox(block);
        if (box == null) return;
        e.setCancelled(true);

        removeBlock(block);
        Player player = e.getPlayer();
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
            String target = BlockCompat.to(block);
            String boxBlock = box.getBlock().contains(":") ? box.getBlock() : "minecraft:" + box.getBlock();
            if (boxBlock.equalsIgnoreCase(target)) {
                return boxConfig.get(pdc.get(key, PersistentDataType.STRING));
            } else pdc.remove(key);
        }
        return null;
    }

}

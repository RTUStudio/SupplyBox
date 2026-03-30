package kr.rtustudio.supplybox.handler;

import kr.rtustudio.framework.bukkit.api.listener.RSListener;
import kr.rtustudio.framework.bukkit.api.registry.CustomItems;
import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.box.BoxInventory;
import kr.rtustudio.supplybox.box.BoxManager;
import kr.rtustudio.supplybox.configuration.BoxConfig;
import kr.rtustudio.supplybox.configuration.LootConfig;
import kr.rtustudio.supplybox.loot.LootManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

@SuppressWarnings("unused")
public class BoxInteract extends RSListener<SupplyBox> {

    private final LootManager lootManager;

    private final NamespacedKey key;
    private final NamespacedKey scheduleKey;

    public BoxInteract(SupplyBox plugin) {
        super(plugin);
        this.lootManager = plugin.getLootManager();
        this.key = new NamespacedKey(plugin, "box");
        this.scheduleKey = plugin.getBoxManager().getScheduleKey();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null || block.getType().isAir()) return;
        BoxConfig box = getBox(block);
        if (box == null || !box.isEnabled()) return;
        
        e.setCancelled(true);
        Player player = e.getPlayer();
        
        if (!box.getItemKey().isEmpty()) {
            ItemStack key = CustomItems.from(box.getItemKey());
            Inventory inventory = player.getInventory();
            if (!inventory.containsAtLeast(key, 1)) {
                notifier.announce(player, message.get(player, "box.noKey"));
                return;
            }
            inventory.removeItem(key);
        }
        
        process(player, block, box);
    }

    private void process(Player player, Block block, BoxConfig box) {
        removeBlock(block);
        LootConfig loot = plugin.getLoots().get(box.getLoot());
        if (loot == null) return;
        List<ItemStack> items = lootManager.getItems(loot);
        switch (box.getInteract()) {
            case DROP -> {
                Location loc = block.getLocation();
                for (ItemStack itemStack : items) loc.getWorld().dropItem(loc, itemStack);
            }
            case INVENTORY -> {
                BoxInventory inventory = new BoxInventory(plugin, box, items);
                player.openInventory(inventory.getInventory());
            }
            case GIVE -> {
                Inventory inventory = player.getInventory();
                for (ItemStack itemStack : items) {
                    if (inventory.firstEmpty() == -1) {
                        player.getWorld().dropItem(player.getLocation(), itemStack);
                    } else inventory.addItem(itemStack);
                }
            }
        }
    }

    private void removeBlock(Block block) {
        if (block.getState() instanceof TileState ts) {
            PersistentDataContainer pdc = ts.getPersistentDataContainer();
            String scheduleName = null;
            if (pdc.has(scheduleKey, PersistentDataType.STRING)) {
                scheduleName = pdc.get(scheduleKey, PersistentDataType.STRING);
                pdc.remove(scheduleKey);
            }
            if (pdc.has(key, PersistentDataType.STRING)) {
                pdc.remove(key);
            }
            ts.update();
            block.setType(Material.AIR);
            if (scheduleName != null) {
                BoxManager boxManager = plugin.getBoxManager();
                boxManager.untrack(block.getWorld(), scheduleName);
                boxManager.onBoxOpened(scheduleName, block.getLocation());
            }
        }
    }

    private BoxConfig getBox(Block block) {
        if (block.getState() instanceof TileState ts) {
            PersistentDataContainer pdc = ts.getPersistentDataContainer();
            if (pdc.has(key, PersistentDataType.STRING)) {
                String boxName = pdc.get(key, PersistentDataType.STRING);
                BoxConfig box = plugin.getBoxes().get(boxName);
                if (box == null) {
                    pdc.remove(key);
                    ts.update();
                    return null;
                }
                return box;
            }
        }
        return null;
    }

}

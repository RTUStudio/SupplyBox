package kr.rtustudio.supplybox.handler;

import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.box.BoxInventory;
import kr.rtustudio.supplybox.box.BoxManager;
import kr.rtustudio.supplybox.configuration.BoxConfig;
import kr.rtustudio.supplybox.configuration.LootConfig;
import kr.rtustudio.supplybox.loot.LootManager;

import kr.rtustudio.framework.bukkit.api.listener.RSListener;
import kr.rtustudio.framework.bukkit.api.registry.CustomItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class BoxInteractEvent extends RSListener<SupplyBox> {

    private final LootManager lootManager;

    private final NamespacedKey key;
    private final NamespacedKey scheduleKey;

    public BoxInteractEvent(SupplyBox plugin) {
        super(plugin);
        this.lootManager = plugin.getLootManager();

        this.key = new NamespacedKey(plugin, "box");
        this.scheduleKey = plugin.getBoxManager().getScheduleKey();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if (block == null || block.getType().isAir()) return;
        BoxConfig box = getBox(block);
        if (box == null) return;
        if (!box.isEnabled()) return;
        e.setCancelled(true);
        if (box.getItemKey().isEmpty()) {
            process(e.getPlayer(), block, box);
        } else {
            ItemStack key = CustomItems.from(box.getItemKey());
            if (player.getInventory().containsAtLeast(key, 1)) {
                player.getInventory().removeItem(key);
                process(e.getPlayer(), block, box);
            } else notifier.announce(player, message.get(player, "box.noKey"));
        }
    }

    private void process(Player player, Block block, BoxConfig box) {
        removeBlock(block);
        LootConfig loot = plugin.getLoots().get(box.getLoot());
        if (loot == null) return;
        List<ItemStack> items = lootManager.getItems(loot);
        switch (box.getInteractType()) {
            case DROP -> {
                Location loc = block.getLocation();
                for (ItemStack itemStack : items) loc.getWorld().dropItem(loc, itemStack);
            }
            case INVENTORY -> {
                BoxInventory inventory = new BoxInventory(plugin, box, items);
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
        if (!(block.getState() instanceof TileState ts)) return;
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
            plugin.getBoxManager().untrackScheduleBox(block.getWorld(), scheduleName);
            plugin.getBoxManager().onBoxOpened(scheduleName, block.getLocation());
        }
    }

    private BoxConfig getBox(Block block) {
        if (!(block.getState() instanceof TileState ts)) return null;
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
        return null;
    }

}

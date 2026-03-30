package kr.rtustudio.supplybox.handler;

import kr.rtustudio.framework.bukkit.api.listener.RSListener;
import kr.rtustudio.framework.bukkit.api.registry.CustomItems;
import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.box.BoxInventory;
import kr.rtustudio.supplybox.configuration.BoxConfig;
import kr.rtustudio.supplybox.configuration.LootConfig;
import kr.rtustudio.supplybox.loot.LootManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ItemInteract extends RSListener<SupplyBox> {

    private final LootManager lootManager;

    public ItemInteract(SupplyBox plugin) {
        super(plugin);
        this.lootManager = plugin.getLootManager();
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR
                && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = e.getItem();
        if (item == null || item.getType().isAir()) return;
        Player player = e.getPlayer();
        
        for (Map.Entry<String, BoxConfig> entry : plugin.getBoxes().asMap().entrySet()) {
            BoxConfig box = entry.getValue();
            ItemStack boxItem = CustomItems.from(box.getItemBox());
            if (boxItem == null || !CustomItems.isSimilar(item, boxItem)) continue;
            
            e.setCancelled(true);
            
            if (!box.getItemKey().isEmpty()) {
                ItemStack key = CustomItems.from(box.getItemKey());
                Inventory inventory = player.getInventory();
                if (!inventory.containsAtLeast(key, 1)) {
                    notifier.announce(player, message.get(player, "box.noKey"));
                    return;
                }
                inventory.removeItem(key);
            }
            
            item.setAmount(item.getAmount() - 1);
            process(player, box);
            return;
        }
    }

    private void process(Player player, BoxConfig box) {
        LootConfig loot = plugin.getLoots().get(box.getLoot());
        if (loot == null) return;
        List<ItemStack> items = lootManager.getItems(loot);
        switch (box.getInteract()) {
            case DROP -> {
                for (ItemStack itemStack : items) player.getWorld().dropItem(player.getLocation(), itemStack);
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

}

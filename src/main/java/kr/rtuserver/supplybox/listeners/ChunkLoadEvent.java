package kr.rtuserver.supplybox.listeners;

import kr.rtuserver.framework.bukkit.api.listener.RSListener;
import kr.rtuserver.supplybox.RSSupplyBox;
import kr.rtuserver.supplybox.box.Box;
import kr.rtuserver.supplybox.box.BoxManager;
import kr.rtuserver.supplybox.configuration.BoxConfig;
import kr.rtuserver.supplybox.configuration.QueueConfig;
import kr.rtuserver.supplybox.data.BlockCoordinate;
import org.bukkit.Chunk;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Map;
import java.util.UUID;

public class ChunkLoadEvent extends RSListener<RSSupplyBox> {

    private final BoxConfig boxConfig;
    private final QueueConfig queueConfig;
    private final BoxManager boxManager;

    private final NamespacedKey key;

    public ChunkLoadEvent(RSSupplyBox plugin) {
        super(plugin);
        this.boxConfig = plugin.getBoxConfig();
        this.queueConfig = plugin.getQueueConfig();
        this.boxManager = plugin.getBoxManager();

        this.key = new NamespacedKey(plugin, "box");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(org.bukkit.event.world.ChunkLoadEvent e) {
        Chunk chunk = e.getChunk();
        UUID world = chunk.getWorld().getUID();
        Map<Long, String> map = queueConfig.get(world);
        boolean queue = false;
        for (Long packed : map.keySet()) {
            Box box = boxConfig.get(map.getOrDefault(packed, ""));
            if (box == null) continue;
            BlockCoordinate pos = new BlockCoordinate(packed);
            if (pos.getChunkKey() == chunk.getChunkKey()) {
                queueConfig.remove(world, packed);
                queue = true;
                int y;
                if (pos.y() == null) {
                    y = chunk.getWorld().getHighestBlockYAt(pos.x(), pos.z(), HeightMap.MOTION_BLOCKING_NO_LEAVES) + 1;
                } else y = pos.y();
                boxManager.setBlock(box, new Location(chunk.getWorld(), pos.x(), y, pos.z()));
            }
        }
        if (queue) queueConfig.save();
    }
}

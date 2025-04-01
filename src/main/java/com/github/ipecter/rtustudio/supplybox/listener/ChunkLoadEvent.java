package com.github.ipecter.rtustudio.supplybox.listener;

import com.github.ipecter.rtustudio.supplybox.SupplyBox;
import com.github.ipecter.rtustudio.supplybox.box.Box;
import com.github.ipecter.rtustudio.supplybox.box.BoxManager;
import com.github.ipecter.rtustudio.supplybox.configuration.BoxConfig;
import com.github.ipecter.rtustudio.supplybox.configuration.QueueConfig;
import com.github.ipecter.rtustudio.supplybox.data.BlockCoordinate;
import kr.rtuserver.framework.bukkit.api.listener.RSListener;
import org.bukkit.Chunk;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Map;
import java.util.UUID;

public class ChunkLoadEvent extends RSListener<SupplyBox> {

    private final BoxConfig boxConfig;
    private final QueueConfig queueConfig;
    private final BoxManager boxManager;

    public ChunkLoadEvent(SupplyBox plugin) {
        super(plugin);
        this.boxConfig = plugin.getBoxConfig();
        this.queueConfig = plugin.getQueueConfig();
        this.boxManager = plugin.getBoxManager();
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

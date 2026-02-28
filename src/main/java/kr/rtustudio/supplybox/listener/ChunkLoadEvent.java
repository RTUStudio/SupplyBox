package kr.rtustudio.supplybox.listener;

import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.box.Box;
import kr.rtustudio.supplybox.box.BoxManager;
import kr.rtustudio.supplybox.configuration.BoxConfig;
import kr.rtustudio.supplybox.configuration.QueueConfig;
import kr.rtustudio.supplybox.data.BlockPos;
import kr.rtustudio.framework.bukkit.api.listener.RSListener;
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
        Map<Long, String[]> map = queueConfig.get(world);
        boolean queue = false;
        for (Map.Entry<Long, String[]> entry : map.entrySet()) {
            Long packed = entry.getKey();
            String[] data = entry.getValue();
            Box box = boxConfig.get(data[0]);
            if (box == null) continue;
            BlockPos pos = new BlockPos(packed);
            long chunkKey = ((long) chunk.getZ() << 32) | (chunk.getX() & 0xFFFFFFFFL);
            if (pos.getChunkKey() == chunkKey) {
                queueConfig.remove(world, packed);
                queue = true;
                int y;
                if (pos.y() == null) {
                    y = chunk.getWorld().getHighestBlockYAt(pos.x(), pos.z(), HeightMap.MOTION_BLOCKING_NO_LEAVES) + 1;
                } else y = pos.y();
                boxManager.setBlock(box, new Location(chunk.getWorld(), pos.x(), y, pos.z()), data[1]);
            }
        }
        if (queue) queueConfig.save();
    }

}

package kr.rtustudio.supplybox.handler;

import kr.rtustudio.supplybox.SupplyBox;
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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChunkLoadEvent extends RSListener<SupplyBox> {

    private final QueueConfig queueConfig;
    private final BoxManager boxManager;

    public ChunkLoadEvent(SupplyBox plugin) {
        super(plugin);
        this.queueConfig = plugin.getQueueConfig();
        this.boxManager = plugin.getBoxManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(org.bukkit.event.world.ChunkLoadEvent e) {
        Chunk chunk = e.getChunk();
        UUID world = chunk.getWorld().getUID();
        Map<Long, String[]> map = queueConfig.get(world);
        long chunkKey = ((long) chunk.getZ() << 32) | (chunk.getX() & 0xFFFFFFFFL);
        List<Long> toRemove = new ObjectArrayList<>();
        for (Map.Entry<Long, String[]> entry : map.entrySet()) {
            Long packed = entry.getKey();
            BlockPos pos = new BlockPos(packed);
            if (pos.getChunkKey() != chunkKey) continue;
            String[] data = entry.getValue();
            String boxName = data[0];
            BoxConfig box = plugin.getBoxes().get(boxName);
            if (box == null) continue;
            toRemove.add(packed);
            int y;
            if (pos.y() == null) {
                y = chunk.getWorld().getHighestBlockYAt(pos.x(), pos.z(), HeightMap.MOTION_BLOCKING_NO_LEAVES) + 1;
            } else y = pos.y();
            boxManager.setBlock(boxName, box, new Location(chunk.getWorld(), pos.x(), y, pos.z()), data[1]);
        }
        for (Long packed : toRemove) queueConfig.remove(world, packed);
        if (!toRemove.isEmpty()) queueConfig.save();
    }

}

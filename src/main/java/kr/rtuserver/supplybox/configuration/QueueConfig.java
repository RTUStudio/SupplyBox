package kr.rtuserver.supplybox.configuration;

import kr.rtuserver.framework.bukkit.api.RSPlugin;
import kr.rtuserver.framework.bukkit.api.config.RSConfiguration;
import kr.rtuserver.supplybox.RSSupplyBox;
import kr.rtuserver.supplybox.data.BlockCoordinate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QueueConfig extends RSConfiguration<RSSupplyBox> {

    private final Map<UUID, Map<Long, String>> queue = new HashMap<>();

    public QueueConfig(RSSupplyBox plugin) {
        super(plugin, "Data", "Queue.yml", null);
        setup(this);
    }

    public void add(UUID world, BlockCoordinate pos, String box) {
        set(world.toString() + "." + pos.getBlockKey(), box);
        Map<Long, String> value = queue.getOrDefault(world, new HashMap<>());
        value.put(pos.getBlockKey(), box);
        queue.put(world, value);
    }

    public Map<Long, String> get(UUID world) {
        return queue.getOrDefault(world, new HashMap<>());
    }

    public void remove(UUID world, long block) {
        Map<Long, String> value = queue.get(world);
        if (value != null) {
            value.remove(block);
            if (value.isEmpty()) queue.remove(world);
        }
        queue.remove(world);
        set(world.toString() + "." + block, null);
    }
}

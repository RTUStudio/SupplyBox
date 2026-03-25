package kr.rtustudio.supplybox.configuration;

import com.google.gson.JsonObject;

import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.data.BlockPos;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.Map;
import java.util.UUID;

public class QueueConfig {

    private static final String TABLE = "Queue";

    private final Map<UUID, Map<Long, String[]>> queue = new Object2ObjectOpenHashMap<>();
    private final SupplyBox plugin;

    public QueueConfig(SupplyBox plugin) {
        this.plugin = plugin;
        reload();
    }

    public void add(UUID world, BlockPos pos, String box, String scheduleName) {
        Map<Long, String[]> value = queue.computeIfAbsent(world, k -> new Long2ObjectOpenHashMap<>());
        value.put(pos.getBlockKey(), new String[]{box, scheduleName});
        JsonObject object = new JsonObject();
        object.addProperty("world", world.toString());
        object.addProperty("block", pos.getBlockKey());
        object.addProperty("box", box);
        if (scheduleName != null) object.addProperty("schedule", scheduleName);
        plugin.getStorage("Queue").add(object);
    }

    public Map<Long, String[]> get(UUID world) {
        return queue.getOrDefault(world, new Long2ObjectOpenHashMap<>());
    }

    public void remove(UUID world, long block) {
        Map<Long, String[]> value = queue.get(world);
        if (value != null) {
            value.remove(block);
            if (value.isEmpty()) queue.remove(world);
        }
        JsonObject filter = new JsonObject();
        filter.addProperty("world", world.toString());
        filter.addProperty("block", block);
        plugin.getStorage("Queue").set(filter, null);
    }

    public void save() {
    }

    public void reload() {
        queue.clear();
        plugin.getStorage("Queue").get(new JsonObject()).thenAccept(result -> {
            for (JsonObject object : result) {
                try {
                    UUID world = UUID.fromString(object.get("world").getAsString());
                    long block = object.get("block").getAsLong();
                    String box = object.get("box").getAsString();
                    if (box.isEmpty()) continue;
                    String schedule = object.has("schedule") ? object.get("schedule").getAsString() : null;
                    queue.computeIfAbsent(world, k -> new Long2ObjectOpenHashMap<>()).put(block, new String[]{box, schedule});
                } catch (Exception ignored) {}
            }
        });
    }
}

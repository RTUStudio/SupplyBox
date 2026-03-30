package kr.rtustudio.supplybox.configuration;

import com.google.gson.JsonObject;
import kr.rtustudio.storage.Storage;
import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.data.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QueueConfig {

    private static final String TABLE = "Queue";

    private final Map<UUID, Map<Long, String[]>> queue = new HashMap<>();
    private final SupplyBox plugin;
    private final Storage storage;

    public QueueConfig(SupplyBox plugin) {
        this.plugin = plugin;
        this.storage = plugin.getStorage(TABLE);
        reload();
    }

    public void add(UUID world, BlockPos pos, String box, String scheduleName) {
        Map<Long, String[]> value = queue.computeIfAbsent(world, k -> new HashMap<>());
        value.put(pos.getBlockKey(), new String[]{box, scheduleName});
        JsonObject object = new JsonObject();
        object.addProperty("world", world.toString());
        object.addProperty("block", pos.getBlockKey());
        object.addProperty("box", box);
        if (scheduleName != null) object.addProperty("schedule", scheduleName);
        storage.add(object);
    }

    public Map<Long, String[]> get(UUID world) {
        return queue.getOrDefault(world, new HashMap<>());
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
        storage.set(filter, new JsonObject());
    }

    public void save() {
    }

    public void reload() {
        queue.clear();
        storage.get(new JsonObject()).thenAccept(result -> {
            for (JsonObject object : result) {
                try {
                    UUID world = UUID.fromString(object.get("world").getAsString());
                    long block = object.get("block").getAsLong();
                    String box = object.get("box").getAsString();
                    if (box.isEmpty()) continue;
                    String schedule = object.has("schedule") ? object.get("schedule").getAsString() : null;
                    queue.computeIfAbsent(world, k -> new HashMap<>()).put(block, new String[]{box, schedule});
                } catch (Exception ignored) {
                }
            }
        });
    }
}

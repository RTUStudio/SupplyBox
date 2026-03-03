package kr.rtustudio.supplybox.box;

import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.configuration.DiscordConfig;
import kr.rtustudio.supplybox.configuration.QueueConfig;
import kr.rtustudio.supplybox.data.BlockPos;
import kr.rtustudio.supplybox.data.WorldCoordinate;
import kr.rtustudio.supplybox.profile.LocationProfile;
import kr.rtustudio.supplybox.profile.Profile;
import kr.rtustudio.supplybox.profile.RegionProfile;
import com.jeff_media.customblockdata.CustomBlockData;
import kr.rtustudio.framework.bukkit.api.configuration.internal.translation.TranslationConfiguration;
import kr.rtustudio.framework.bukkit.api.format.ComponentFormatter;
import kr.rtustudio.framework.bukkit.api.player.PlayerChat;
import kr.rtustudio.framework.bukkit.api.registry.CustomBlocks;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class BoxManager {

    private final SupplyBox plugin;

    private final QueueConfig queueConfig;

    private final NamespacedKey key;
    private final NamespacedKey scheduleKey;

    private final TranslationConfiguration translation;

    private java.util.function.BiConsumer<String, Location> boxOpenedCallback;

    public BoxManager(SupplyBox plugin) {
        this.plugin = plugin;
        this.queueConfig = plugin.getQueueConfig();
        this.key = new NamespacedKey(plugin, "box");
        this.scheduleKey = new NamespacedKey(plugin, "schedule");
        this.translation = plugin.getConfiguration().getMessage();
    }

    public void setBoxOpenedCallback(java.util.function.BiConsumer<String, Location> callback) {
        this.boxOpenedCallback = callback;
    }

    public void spawn(Box box, Profile profile) {
        spawn(box, profile, null);
    }

    public void spawn(Box box, Profile profile, String scheduleName) {
        getLocation(profile).thenAccept(locations -> {
            if (locations == null || locations.isEmpty()) return;
            List<Location> result = new ArrayList<>();
            boolean queue = false;
            for (WorldCoordinate loc : locations) {
                World world = Bukkit.getWorld(loc.world());
                if (world == null) continue;
                int y = loc.pos().y() != null ? loc.pos().y() : 0;
                Location location = new Location(world, loc.pos().x(), y, loc.pos().z());
                if (location.getChunk().isLoaded()) {
                    if (loc.pos().y() == null) {
                        int highestY = world.getHighestBlockYAt(loc.pos().x(), loc.pos().z(), HeightMap.MOTION_BLOCKING_NO_LEAVES) + 1;
                        location.setY(highestY);
                    }
                    result.add(location);
                } else {
                    queueConfig.add(world.getUID(), new BlockPos(loc.pos().x(), loc.pos().y(), loc.pos().z()), box.getName(), scheduleName);
                    queue = true;
                }
                alert(box, loc);
            }
            if (queue) queueConfig.save();
            for (Location location : result) setBlock(box, location, scheduleName);
        });
    }

    private CompletableFuture<List<WorldCoordinate>> getLocation(Profile profile) {
        return CompletableFuture.supplyAsync(() -> {
            if (profile instanceof LocationProfile lProfile) {
                List<WorldCoordinate> list = lProfile.getLocations();
                int select = lProfile.getSelect();
                if (list.size() <= select) return list;
                else {
                    List<Integer> indexes = new Random().ints(select, 0, list.size())
                            .distinct().boxed().toList();
                    List<WorldCoordinate> result = new ArrayList<>();
                    for (Integer index : indexes) result.add(list.get(index));
                    return result;
                }
            } else if (profile instanceof RegionProfile rProfile) {
                int amount = rProfile.getAmount();
                List<WorldCoordinate> result = new ArrayList<>();
                switch (rProfile.getType()) {
                    case CIRCLE -> {
                        for (int i = 0; i < amount; i++) {
                            double a = Math.random() * 2 * Math.PI;
                            double r = rProfile.getRadius() * Math.sqrt(Math.random());
                            int x = (int) Math.round(r * Math.cos(a));
                            int z = (int) Math.round(r * Math.sin(a));
                            result.add(new WorldCoordinate(rProfile.getWorld(), new BlockPos(x + rProfile.getCenterX(), null, z + rProfile.getCenterZ())));
                        }
                    }
                    case SQUARE -> {
                        Random random = new Random();
                        for (int i = 0; i < amount; i++) {
                            int x = random.nextInt(rProfile.getRadius() * 2) - rProfile.getRadius();
                            int z = random.nextInt(rProfile.getRadius() * 2) - rProfile.getRadius();
                            result.add(new WorldCoordinate(rProfile.getWorld(), new BlockPos(x + rProfile.getCenterX(), null, z + rProfile.getCenterZ())));
                        }
                    }
                }
                return result;
            }
            return null;
        });
    }

    public void setBlock(Box box, Location loc) {
        setBlock(box, loc, null);
    }

    public void setBlock(Box box, Location loc, String scheduleName) {
        BlockData blockData = CustomBlocks.from(box.getBlock());
        if (blockData == null) blockData = Material.CHEST.createBlockData();
        if (Bukkit.getWorld(loc.getWorld().getUID()) == null) return;
        BlockData copy = blockData;
        plugin.getFramework().getScheduler().sync(loc, () -> {
            Block block = loc.getWorld().getBlockAt(loc);
            block.setBlockData(copy);
            PersistentDataContainer pdc = new CustomBlockData(block, plugin);
            pdc.set(key, PersistentDataType.STRING, box.getName());
            if (scheduleName != null) {
                pdc.set(scheduleKey, PersistentDataType.STRING, scheduleName);
            }
        });
    }

    public void onBoxOpened(String scheduleName, Location loc) {
        if (scheduleName == null) return;
        if (boxOpenedCallback != null) {
            boxOpenedCallback.accept(scheduleName, loc);
        }
    }

    public NamespacedKey getScheduleKey() {
        return scheduleKey;
    }

    public void clearScheduleTracking() {
        // No-op as global tracking is removed
    }

    private String formatMessage(String template, Box box, String display, WorldCoordinate pos) {
        return template
                .replace("[display]", display)
                .replace("[name]", box.getName())
                .replace("[world]", pos.world())
                .replace("[x]", String.valueOf(pos.pos().x()))
                .replace("[y]", String.valueOf(pos.pos().y()))
                .replace("[z]", String.valueOf(pos.pos().z()));
    }

    private void alert(Box box, WorldCoordinate pos) {
        if (box.isAlertMinecraft()) {
            PlayerChat chat = PlayerChat.of(plugin);
            for (Player player : Bukkit.getOnlinePlayers()) {
                String message = formatMessage(translation.get(player, "box.spawn"), box, box.getDisplay(), pos);
                chat.announce(player, message);
            }
        }
        
        if (box.isAlertDiscord()) {
            DiscordConfig discordConfig = plugin.getConfiguration(DiscordConfig.class);
            if (discordConfig != null && !discordConfig.getMessage().isEmpty()) {
                String display = PlainTextComponentSerializer.plainText().serialize(ComponentFormatter.mini(box.getDisplay()));
                String discordMessage = formatMessage(discordConfig.getMessage(), box, display, pos);
                plugin.getDiscord().sendMessage(discordMessage);
            }
        }
    }

}

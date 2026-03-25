package kr.rtustudio.supplybox.box;

import kr.rtustudio.supplybox.SupplyBox;
import kr.rtustudio.supplybox.configuration.BoxConfig;
import kr.rtustudio.supplybox.configuration.DiscordConfig;
import kr.rtustudio.supplybox.configuration.LootConfig;
import kr.rtustudio.supplybox.configuration.ProfileConfig;
import kr.rtustudio.supplybox.configuration.QueueConfig;
import kr.rtustudio.supplybox.data.BlockPos;
import kr.rtustudio.supplybox.data.WorldCoordinate;

import kr.rtustudio.framework.bukkit.api.configuration.internal.translation.TranslationConfiguration;
import kr.rtustudio.framework.bukkit.api.format.ComponentFormatter;

import kr.rtustudio.framework.bukkit.api.registry.CustomBlocks;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class BoxManager {

    private final SupplyBox plugin;

    private final QueueConfig queueConfig;

    private final NamespacedKey key;
    private final NamespacedKey scheduleKey;

    private final TranslationConfiguration translation;

    private final Set<String> activeSchedules = new ObjectOpenHashSet<>();
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

    public void spawn(String boxName, BoxConfig box, ProfileConfig profile) {
        spawn(boxName, box, profile, null);
    }

    public void spawn(String boxName, BoxConfig box, ProfileConfig profile, String scheduleName) {
        getLocation(profile).thenAccept(locations -> {
            if (locations == null || locations.isEmpty()) return;
            List<Location> result = new ObjectArrayList<>();
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
                    queueConfig.add(world.getUID(), new BlockPos(loc.pos().x(), loc.pos().y(), loc.pos().z()), boxName, scheduleName);
                    queue = true;
                }
                alert(boxName, box, loc);
            }
            if (queue) queueConfig.save();
            for (Location location : result) setBlock(boxName, box, location, scheduleName);
        });
    }

    private CompletableFuture<List<WorldCoordinate>> getLocation(ProfileConfig profile) {
        return CompletableFuture.supplyAsync(() -> {
            if (profile.isLocation()) {
                ProfileConfig.Location loc = profile.getLocation();
                List<String> rawList = loc.getList();
                List<WorldCoordinate> parsed = new ObjectArrayList<>();
                for (String str : rawList) {
                    try {
                        String[] split = str.split(",");
                        if (split.length < 3) continue;
                        if (split.length == 3) {
                            int x = Integer.parseInt(split[0].trim());
                            int z = Integer.parseInt(split[2].trim());
                            parsed.add(new WorldCoordinate("world", new BlockPos(x, null, z)));
                        } else {
                            int x = Integer.parseInt(split[1].trim());
                            int y = Integer.parseInt(split[2].trim());
                            int z = Integer.parseInt(split[3].trim());
                            parsed.add(new WorldCoordinate(split[0].trim(), new BlockPos(x, y, z)));
                        }
                    } catch (NumberFormatException ignored) {}
                }
                int select = loc.getSelect();
                if (parsed.size() <= select) return parsed;
                List<Integer> indexes = new Random().ints(select, 0, parsed.size())
                        .distinct().boxed().toList();
                List<WorldCoordinate> selected = new ObjectArrayList<>();
                for (Integer index : indexes) selected.add(parsed.get(index));
                return selected;
            } else if (profile.isRegion()) {
                ProfileConfig.Region reg = profile.getRegion();
                int amount = reg.getAmount();
                List<WorldCoordinate> result = new ObjectArrayList<>();
                switch (reg.getRegionType()) {
                    case CIRCLE -> {
                        for (int i = 0; i < amount; i++) {
                            double a = Math.random() * 2 * Math.PI;
                            double r = reg.getRadius() * Math.sqrt(Math.random());
                            int x = (int) Math.round(r * Math.cos(a));
                            int z = (int) Math.round(r * Math.sin(a));
                            result.add(new WorldCoordinate(reg.getWorld(), new BlockPos(x + reg.getCenterX(), null, z + reg.getCenterZ())));
                        }
                    }
                    case SQUARE -> {
                        Random random = new Random();
                        for (int i = 0; i < amount; i++) {
                            int x = random.nextInt(reg.getRadius() * 2) - reg.getRadius();
                            int z = random.nextInt(reg.getRadius() * 2) - reg.getRadius();
                            result.add(new WorldCoordinate(reg.getWorld(), new BlockPos(x + reg.getCenterX(), null, z + reg.getCenterZ())));
                        }
                    }
                }
                return result;
            }
            return null;
        });
    }

    public void setBlock(String boxName, BoxConfig box, Location loc) {
        setBlock(boxName, box, loc, null);
    }

    public void setBlock(String boxName, BoxConfig box, Location loc, String scheduleName) {
        BlockData blockData = CustomBlocks.from(box.getBlock());
        if (blockData == null) blockData = Material.CHEST.createBlockData();
        if (Bukkit.getWorld(loc.getWorld().getUID()) == null) return;
        BlockData copy = blockData;
        plugin.getFramework().getScheduler().sync(loc, () -> {
            Block block = loc.getWorld().getBlockAt(loc);
            block.setBlockData(copy);
            if (block.getState() instanceof org.bukkit.block.TileState ts) {
                PersistentDataContainer pdc = ts.getPersistentDataContainer();
                pdc.set(key, PersistentDataType.STRING, boxName);
                if (scheduleName != null) {
                    pdc.set(scheduleKey, PersistentDataType.STRING, scheduleName);
                    PersistentDataContainer worldPdc = loc.getWorld().getPersistentDataContainer();
                    NamespacedKey countKey = scheduleCountKey(scheduleName);
                    int current = worldPdc.getOrDefault(countKey, PersistentDataType.INTEGER, 0);
                    worldPdc.set(countKey, PersistentDataType.INTEGER, current + 1);
                }
                ts.update();
            }
        });
        if (scheduleName != null) {
            activeSchedules.add(scheduleName);
        }
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

    public void loadScheduleBoxes() {
        activeSchedules.clear();
        kr.rtustudio.supplybox.configuration.ScheduleConfig config = plugin.getConfiguration(kr.rtustudio.supplybox.configuration.ScheduleConfig.class);
        if (config == null) return;
        for (String name : config.getSchedules().keySet()) {
            NamespacedKey countKey = scheduleCountKey(name);
            for (World world : Bukkit.getWorlds()) {
                Integer count = world.getPersistentDataContainer()
                        .get(countKey, PersistentDataType.INTEGER);
                if (count != null && count > 0) {
                    activeSchedules.add(name);
                    break;
                }
            }
        }
    }

    public boolean hasActiveScheduleBoxes(String scheduleName) {
        return activeSchedules.contains(scheduleName);
    }

    public void untrackScheduleBox(World world, String scheduleName) {
        NamespacedKey countKey = scheduleCountKey(scheduleName);
        PersistentDataContainer worldPdc = world.getPersistentDataContainer();
        int current = worldPdc.getOrDefault(countKey, PersistentDataType.INTEGER, 0);
        if (current <= 1) {
            worldPdc.remove(countKey);
        } else {
            worldPdc.set(countKey, PersistentDataType.INTEGER, current - 1);
        }
    }

    private NamespacedKey scheduleCountKey(String scheduleName) {
        return new NamespacedKey(plugin, "sched_" + scheduleName.toLowerCase().replaceAll("[^a-z0-9._-]", "_"));
    }

    private String formatMessage(String template, String boxName, String display, WorldCoordinate pos) {
        return template
                .replace("[display]", display)
                .replace("[name]", boxName)
                .replace("[world]", pos.world())
                .replace("[x]", String.valueOf(pos.pos().x()))
                .replace("[y]", String.valueOf(pos.pos().y()))
                .replace("[z]", String.valueOf(pos.pos().z()));
    }

    private void alert(String boxName, BoxConfig box, WorldCoordinate pos) {
        if (box.isAlertMinecraft()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String message = formatMessage(translation.get(player, "box.spawn"), boxName, box.getDisplayName(), pos);
                player.sendMessage(ComponentFormatter.mini(message));
            }
        }

        if (box.isAlertDiscord()) {
            DiscordConfig discordConfig = plugin.getConfiguration(DiscordConfig.class);
            if (discordConfig != null && !discordConfig.getMessage().isEmpty()) {
                String display = PlainTextComponentSerializer.plainText().serialize(ComponentFormatter.mini(box.getDisplayName()));
                String discordMessage = formatMessage(discordConfig.getMessage(), boxName, display, pos);
                plugin.getDiscord().sendMessage(discordMessage);
            }
        }
    }

}

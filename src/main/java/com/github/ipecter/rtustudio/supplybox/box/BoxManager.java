package com.github.ipecter.rtustudio.supplybox.box;

import com.github.ipecter.rtustudio.supplybox.SupplyBox;
import com.github.ipecter.rtustudio.supplybox.configuration.DiscordConfig;
import com.github.ipecter.rtustudio.supplybox.configuration.QueueConfig;
import com.github.ipecter.rtustudio.supplybox.data.BlockCoordinate;
import com.github.ipecter.rtustudio.supplybox.data.WorldCoordinate;
import com.github.ipecter.rtustudio.supplybox.profile.LocationProfile;
import com.github.ipecter.rtustudio.supplybox.profile.Profile;
import com.github.ipecter.rtustudio.supplybox.profile.RegionProfile;
import com.jeff_media.customblockdata.CustomBlockData;
import kr.rtuserver.framework.bukkit.api.configuration.translation.TranslationConfiguration;
import kr.rtuserver.framework.bukkit.api.format.ComponentFormatter;
import kr.rtuserver.framework.bukkit.api.player.PlayerChat;
import kr.rtuserver.framework.bukkit.api.registry.CustomBlocks;
import kr.rtuserver.framework.bukkit.api.scheduler.CraftScheduler;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class BoxManager {

    private final SupplyBox plugin;

    private final DiscordConfig discordConfig;
    private final QueueConfig queueConfig;

    private final NamespacedKey key;

    private final TranslationConfiguration translation;

    public BoxManager(SupplyBox plugin) {
        this.plugin = plugin;
        this.discordConfig = plugin.getDiscordConfig();
        this.queueConfig = plugin.getQueueConfig();
        this.key = new NamespacedKey(plugin, "box");
        this.translation = plugin.getConfigurations().getMessage();
    }

    public void spawn(Box box, Profile profile) {
        getLocation(profile).thenAccept(locations -> {
            List<Location> result = new ArrayList<>();
            if (locations == null) return;
            boolean queue = false;
            for (WorldCoordinate loc : locations) {
                World world = Bukkit.getWorld(loc.world());
                if (world == null) continue;
                Location location = new Location(world, loc.pos().x(), 0, loc.pos().z());
                if (location.isChunkLoaded()) {
                    if (loc.pos().y() == null) {
                        int highestY = world.getHighestBlockYAt(loc.pos().x(), loc.pos().z(), HeightMap.MOTION_BLOCKING_NO_LEAVES) + 1;
                        location.setY(highestY);
                    }
                    result.add(location);
                } else {
                    queueConfig.add(world.getUID(), new BlockCoordinate(loc.pos().x(), loc.pos().y(), loc.pos().z()), box.getName());
                    queue = true;
                }
                alert(box, loc);
            }
            if (queue) queueConfig.save();
            for (Location location : result) setBlock(box, location);
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
                            result.add(new WorldCoordinate(rProfile.getWorld(), new BlockCoordinate(x + rProfile.getCenterX(), null, z + rProfile.getCenterZ())));
                        }
                    }
                    case SQUARE -> {
                        Random random = new Random();
                        for (int i = 0; i < amount; i++) {
                            int x = random.nextInt(rProfile.getRadius() * 2) - rProfile.getRadius();
                            int z = random.nextInt(rProfile.getRadius() * 2) - rProfile.getRadius();
                            result.add(new WorldCoordinate(rProfile.getWorld(), new BlockCoordinate(x + rProfile.getCenterX(), null, z + rProfile.getCenterZ())));
                        }
                    }
                }
                return result;
            }
            return null;
        });
    }

    public void setBlock(Box box, Location loc) {
        BlockData blockData = CustomBlocks.from(box.getBlock());
        if (blockData == null) blockData = Material.CHEST.createBlockData();
        if (Bukkit.getWorld(loc.getWorld().getUID()) == null) return;
        BlockData copy = blockData;
        CraftScheduler.run(plugin, () -> {
            Block block = loc.getWorld().getBlockAt(loc);
            block.setBlockData(copy);
            PersistentDataContainer pdc = new CustomBlockData(block, plugin);
            pdc.set(key, PersistentDataType.STRING, box.getName());
        });
    }

    private void alert(Box box, WorldCoordinate pos) {
        PlayerChat chat = PlayerChat.of(plugin);
        for (Player player : Bukkit.getOnlinePlayers()) {
            String message = translation.get(player, "box.spawn")
                    .replace("[display]", box.getDisplay())
                    .replace("[name]", box.getName())
                    .replace("[world]", pos.world())
                    .replace("[x]", String.valueOf(pos.pos().x()))
                    .replace("[y]", String.valueOf(pos.pos().y()))
                    .replace("[z]", String.valueOf(pos.pos().z()));
            chat.announce(player, message);
        }
        String display = PlainTextComponentSerializer.plainText().serialize(ComponentFormatter.mini(box.getDisplay()));
        String message = discordConfig.getMessage()
                .replace("[display]", display)
                .replace("[name]", box.getName())
                .replace("[world]", pos.world())
                .replace("[x]", String.valueOf(pos.pos().x()))
                .replace("[y]", String.valueOf(pos.pos().y()))
                .replace("[z]", String.valueOf(pos.pos().z()));
        plugin.getHooks().getDiscord().sendMessage(message);
    }

}

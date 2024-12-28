package kr.rtuserver.supplybox.commands;

import kr.rtuserver.framework.bukkit.api.command.RSCommand;
import kr.rtuserver.framework.bukkit.api.command.RSCommandData;
import kr.rtuserver.supplybox.RSSupplyBox;
import kr.rtuserver.supplybox.box.BoxManager;
import kr.rtuserver.supplybox.configuration.*;
import kr.rtuserver.supplybox.schedule.ScheduleManager;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Command extends RSCommand<RSSupplyBox> {

    private final LootConfig lootConfig;
    private final BoxConfig boxConfig;
    private final ProfileConfig profileConfig;
    private final ScheduleConfig scheduleConfig;
    private final QueueConfig queueConfig;


    private final BoxManager boxManager;
    private final ScheduleManager scheduleManager;

    public Command(RSSupplyBox plugin) {
        super(plugin, "rssb", true);
        this.lootConfig = plugin.getLootConfig();
        this.boxConfig = plugin.getBoxConfig();
        this.profileConfig = plugin.getProfileConfig();
        this.scheduleConfig = plugin.getScheduleConfig();
        this.queueConfig = plugin.getQueueConfig();
        this.boxManager = plugin.getBoxManager();
        this.scheduleManager = plugin.getScheduleManager();
    }

//    @Override
//    public boolean execute(RSCommandData data) {
//        PlayerChat chat = PlayerChat.of(getPlugin());
//        if (data.length(4) && data.equals(0, getCommand().get(getSender(), "spawn"))) {
//            if (getSender() instanceof Player player) {
//                if (hasPermission("urb.spawn")) {
//                    Box box = boxManager.boxMap.get(data.args(1));
//                    if (box != null) {
//                        String value = data.args(3);
//                        try {
//                            switch (data.args(2)) {
//                                case "location" -> {
//                                    String[] values = value.split("/");
//                                    Location location = new Location(player.getWorld(), Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
//                                    boxManager.spawn(box, location);
//                                    return true;
//                                }
//                                case "player" -> {
//                                    Player player1 = Bukkit.getPlayer(value);
//                                    boxManager.spawn(box, player1.getLocation());
//                                    return true;
//                                }
//                                case "random" -> {
//                                    Profile profile = ProfileManager.getInstance().profileMap.get(value);
//                                    if (profile != null) {
//                                        CraftScheduler.runAsync(plugin, new ScheduleRunnable(box, profile));
//                                        return true;
//                                    }
//                                }
//                            }
//                        } catch (Exception e) {
//                            sender.sendMessage(textManager.formatted(player, configManager.getTranslation("prefix") + configManager.getTranslation("commandSpawnWrongUsage")));
//                        }
//                        sender.sendMessage(textManager.formatted(player, configManager.getTranslation("prefix") + configManager.getTranslation("commandSpawnWrongUsage")));
//                        return true;
//                    } else {
//                        sender.sendMessage(textManager.formatted(player, configManager.getTranslation("prefix") + configManager.getTranslation("noBox")));
//                    }
//                } else {
//                    sender.sendMessage(textManager.formatted(configManager.getTranslation("prefix") + configManager.getTranslation("noPermission")));
//                }
//            } else {
//                sender.sendMessage(textManager.formatted(configManager.getTranslation("prefix") + configManager.getTranslation("commandWrongUsageConsole")));
//            }
//            return true;
//        } else if (args.length >= 2 && data.equals(0, getCommand().get(getSender(), "spawnhere"))) {
//            if (sender instanceof Player player) {
//                if (sender.hasPermission("urb.spawnhere")) {
//                    Box box = boxManager.boxMap.get(args[1]);
//                    if (box != null) {
//                        boxManager.spawn(box, player.getLocation());
//                    } else {
//                        sender.sendMessage(textManager.formatted(player, configManager.getTranslation("prefix") + configManager.getTranslation("noBox")));
//                    }
//                } else {
//                    sender.sendMessage(textManager.formatted(configManager.getTranslation("prefix") + configManager.getTranslation("noPermission")));
//                }
//            } else {
//                sender.sendMessage(textManager.formatted(configManager.getTranslation("prefix") + configManager.getTranslation("commandWrongUsageConsole")));
//            }
//            return true;
//        } else if (args.length >= 2 && data.equals(0, getCommand().get(getSender(), "open"))) {
//            if (sender instanceof Player player) {
//                if (sender.hasPermission("urb.open")) {
//                    Box box = boxManager.boxMap.get(args[1]);
//                    if (box != null) {
//                        take(player, box);
//                        sender.sendMessage(textManager.formatted(player, configManager.getTranslation("prefix") + box.getOpenMessage()));
//                    } else {
//                        sender.sendMessage(textManager.formatted(player, configManager.getTranslation("prefix") + configManager.getTranslation("noBox")));
//                    }
//                } else {
//                    sender.sendMessage(textManager.formatted(configManager.getTranslation("prefix") + configManager.getTranslation("noPermission")));
//                }
//            } else {
//                sender.sendMessage(textManager.formatted(configManager.getTranslation("prefix") + configManager.getTranslation("commandWrongUsageConsole")));
//            }
//            return true;
//        } else if (args.length >= 1 && data.equals(0, getCommand().get(getSender(), "list"))) {
//            if (sender instanceof Player player) {
//                if (sender.hasPermission("urb.list")) {
//                    sender.sendMessage(textManager.formatted(configManager.getTranslation("prefix") + "<gray>" + String.join("</gray> | <gray>", boxManager.spawnList)));
//                } else {
//                    sender.sendMessage(textManager.formatted(configManager.getTranslation("prefix") + configManager.getTranslation("noPermission")));
//                }
//            } else {
//                sender.sendMessage(textManager.formatted(configManager.getTranslation("prefix") + configManager.getTranslation("commandWrongUsageConsole")));
//            }
//            return true;
//        }
//        sender.sendMessage(textManager.formatted(sender instanceof Player ? (Player) sender : null, configManager.getTranslation("prefix") + configManager.getTranslation("commandWrongUsage")));
//        return true;
//    }
//
//    @Override
//    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String
//            alias, String[] args) {
//        if (args.length == 1) {
//            List<String> list = new ArrayList<>();
//            if (sender.isOp()) list.add("test");
//            if (sender.hasPermission("urb.reload")) list.add("reload");
//            if (sender.hasPermission("urb.spawn")) list.add("spawn");
//            if (sender.hasPermission("urb.spawnhere")) list.add("spawnhere");
//            if (sender.hasPermission("urb.list")) list.add("list");
//            if (sender.hasPermission("urb.open")) list.add("open");
//            return list;
//        } else if (args.length >= 2 && args[0].equalsIgnoreCase("spawn")) {
//            if (args.length >= 4) {
//                switch (args[2]) {
//                    case "location" -> {
//                        return List.of("0/0/0");
//                    }
//                    case "player" -> {
//                        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
//                    }
//                    case "random" -> {
//                        return new ArrayList<>(ProfileManager.getInstance().profileMap.keySet());
//                    }
//                    default -> {
//                        return List.of("잘못된스폰타입");
//                    }
//                }
//            } else if (args.length >= 3) {
//                return List.of("location", "player", "random");
//            } else {
//                return new ArrayList<>(boxManager.boxMap.keySet());
//            }
//        } else if (args.length >= 2 && (args[0].equalsIgnoreCase("spawnhere") || args[0].equalsIgnoreCase("open"))) {
//            return new ArrayList<>(boxManager.boxMap.keySet());
//        }
//        return List.of();
//    }
//
//    private void take(Player player, Box box) {
//        if (box.isFastTake()) {
//            Random random = new Random();
//            for (ItemStack itemStack : RandomUtil.boxItem(random, box, new ArrayList<>())) {
//                if (player.getInventory().firstEmpty() == -1) {
//                    player.getWorld().dropItem(player.getLocation(), itemStack);
//                } else {
//                    player.getInventory().addItem(itemStack);
//                }
//            }
//        } else {
//            player.openInventory(new BoxInvenrory(box).getInventory());
//        }
//    }


    @Override
    protected boolean execute(RSCommandData data) {
        return true;
    }

    @Override
    protected void reload(RSCommandData data) {
        scheduleManager.stop();
        lootConfig.reload();
        boxConfig.reload();
        profileConfig.reload();
        scheduleConfig.reload();
        queueConfig.reload();
        scheduleManager.start();
    }

    @Override
    protected List<String> tabComplete(RSCommandData data) {
        return List.of();
    }
}

package me.voseraproject.randomItemChange;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RicCommand implements CommandExecutor {

    private final RandomItemChange plugin;

    public RicCommand(RandomItemChange plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = plugin.getRawLangMessage("prefix");

        if (args.length == 0) {
            String msg = plugin.getRawLangMessage("invalid-usage");
            sender.sendMessage(prefix + msg.replace("|", " "));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "start":
                if (!sender.hasPermission("ric.start")) {
                    sendMessageOrTitle(sender, "no-permission", prefix);
                    return true;
                }
                if (plugin.isActive()) {
                    sendMessageOrTitle(sender, "event-already-started", prefix);
                } else {
                    plugin.startEvent();
                    plugin.sendTitleToAll(plugin.getRawLangMessage("event-started"));
                }
                break;

            case "stop":
                if (!sender.hasPermission("ric.stop")) {
                    sendMessageOrTitle(sender, "no-permission", prefix);
                    return true;
                }
                if (!plugin.isActive()) {
                    sendMessageOrTitle(sender, "event-already-stopped", prefix);
                } else {
                    plugin.stopEvent();
                    plugin.sendTitleToAll(plugin.getRawLangMessage("event-stopped"));
                }
                break;

            case "duration":
                if (!sender.hasPermission("ric.duration")) {
                    sendMessageOrTitle(sender, "no-permission", prefix);
                    return true;
                }
                if (args.length < 2) {
                    sendMessageOrTitle(sender, "invalid-number", prefix);
                    return true;
                }
                try {
                    int newDuration = Integer.parseInt(args[1]);
                    if (newDuration <= 0) throw new NumberFormatException();

                    plugin.setDuration(newDuration);

                    String msg = plugin.getRawLangMessage("duration-updated").replace("{time}", String.valueOf(newDuration));
                    if (sender instanceof Player) {
                        plugin.sendTitle((Player) sender, msg);
                    } else {
                        sender.sendMessage(prefix + msg.replace("|", " "));
                    }
                } catch (NumberFormatException e) {
                    sendMessageOrTitle(sender, "invalid-number", prefix);
                }
                break;

            case "blacklist":
                if (!sender.hasPermission("ric.blacklist")) {
                    sendMessageOrTitle(sender, "no-permission", prefix);
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(prefix + plugin.getRawLangMessage("blacklist-usage"));
                    return true;
                }

                String action = args[1].toLowerCase();

                if (action.equals("list")) {
                    sender.sendMessage(prefix + plugin.getRawLangMessage("blacklist-list-title"));
                    if (plugin.getBlacklist().isEmpty()) {
                        sender.sendMessage(plugin.getRawLangMessage("blacklist-list-empty"));
                    } else {
                        String format = plugin.getRawLangMessage("blacklist-list-format");
                        for (String item : plugin.getBlacklist()) {
                            sender.sendMessage(format.replace("{item}", item));
                        }
                    }
                    return true;
                }

                String matName = "";
                if (args.length >= 3) {
                    matName = args[2].toUpperCase();
                } else if (sender instanceof Player) {
                    Player p = (Player) sender;
                    ItemStack hand = p.getInventory().getItemInMainHand();
                    if (hand != null && hand.getType() != Material.AIR) {
                        matName = hand.getType().name();
                    }
                }

                if (matName.isEmpty()) {
                    sender.sendMessage(prefix + plugin.getRawLangMessage("blacklist-specify-item"));
                    return true;
                }

                Material mat = Material.getMaterial(matName);
                if (mat == null) {
                    sender.sendMessage(prefix + plugin.getRawLangMessage("blacklist-invalid-item"));
                    return true;
                }

                if (action.equals("add")) {
                    if (plugin.getBlacklist().contains(mat.name())) {
                        sender.sendMessage(prefix + plugin.getRawLangMessage("blacklist-already-exists").replace("{item}", mat.name()));
                    } else {
                        plugin.addToBlacklist(mat.name());
                        sender.sendMessage(prefix + plugin.getRawLangMessage("blacklist-added").replace("{item}", mat.name()));
                    }
                } else if (action.equals("remove")) {
                    if (!plugin.getBlacklist().contains(mat.name())) {
                        sender.sendMessage(prefix + plugin.getRawLangMessage("blacklist-not-found").replace("{item}", mat.name()));
                    } else {
                        plugin.removeFromBlacklist(mat.name());
                        sender.sendMessage(prefix + plugin.getRawLangMessage("blacklist-removed").replace("{item}", mat.name()));
                    }
                } else {
                    sender.sendMessage(prefix + plugin.getRawLangMessage("blacklist-usage"));
                }
                break;

            case "reload":
                if (!sender.hasPermission("ric.reload")) {
                    sendMessageOrTitle(sender, "no-permission", prefix);
                    return true;
                }
                plugin.loadFiles();
                sendMessageOrTitle(sender, "plugin-reloaded", prefix);
                break;

            default:
                String msg = plugin.getRawLangMessage("invalid-usage");
                sender.sendMessage(prefix + msg.replace("|", " "));
                break;
        }

        return true;
    }

    private void sendMessageOrTitle(CommandSender sender, String configKey, String prefix) {
        String msg = plugin.getRawLangMessage(configKey);
        if (sender instanceof Player) {
            plugin.sendTitle((Player) sender, msg);
        } else {
            sender.sendMessage(prefix + msg.replace("|", " "));
        }
    }
}
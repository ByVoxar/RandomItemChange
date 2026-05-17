package me.voseraproject.randomItemChange;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
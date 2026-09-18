package org.easyitems.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.easyitems.EasyItemsPlugin;
import org.easyitems.item.CustomItem;
import org.easyitems.item.ItemManager;

import java.util.ArrayList;
import java.util.List;

public final class EisCommand implements CommandExecutor, TabCompleter {
    private final EasyItemsPlugin plugin;

    public EisCommand(EasyItemsPlugin plugin) {
        this.plugin = plugin;
    }

    private ItemManager items() {
        return plugin.getItemManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.AQUA + "EasyItems " + ChatColor.GRAY + "0.1.0");
            sender.sendMessage(ChatColor.GRAY + "/eis give <player> <item> [amount]");
            sender.sendMessage(ChatColor.GRAY + "/eis list");
            sender.sendMessage(ChatColor.GRAY + "/eis info <item>");
            sender.sendMessage(ChatColor.GRAY + "/eis reload");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!sender.hasPermission("easyitems.admin")) {
                    sender.sendMessage(ChatColor.RED + "No permission.");
                    return true;
                }
                plugin.reloadItems();
                sender.sendMessage(ChatColor.GREEN + "EasyItems reloaded. "
                        + items().size() + " item(s) loaded.");
                return true;
            }
            case "list" -> {
                sender.sendMessage(ChatColor.AQUA + "EasyItems items:");
                for (String id : items().ids()) {
                    sender.sendMessage(ChatColor.GRAY + "- "
                            + ChatColor.WHITE + "eis:" + id);
                }
                return true;
            }
            case "info" -> {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /eis info <item>");
                    return true;
                }
                CustomItem item = items().get(args[1]);
                if (item == null) {
                    sender.sendMessage(ChatColor.RED + "Unknown item: " + args[1]);
                    return true;
                }
                sender.sendMessage(ChatColor.AQUA + "ID: "
                        + ChatColor.WHITE + item.getFullId());
                return true;
            }
            case "give" -> {
                if (!sender.hasPermission("easyitems.give")) {
                    sender.sendMessage(ChatColor.RED + "No permission.");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED
                            + "Usage: /eis give <player> <item> [amount]");
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found: " + args[1]);
                    return true;
                }

                CustomItem item = items().get(args[2]);
                if (item == null) {
                    sender.sendMessage(ChatColor.RED + "Unknown item: " + args[2]);
                    return true;
                }

                int amount = 1;
                if (args.length >= 4) {
                    try {
                        amount = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Invalid amount.");
                        return true;
                    }
                }

                amount = Math.max(1, Math.min(amount, 99));
                target.getInventory().addItem(item.create(amount));
                sender.sendMessage(ChatColor.GREEN + "Gave " + target.getName()
                        + " " + amount + "x " + item.getFullId());
                return true;
            }
            default -> {
                sender.sendMessage(ChatColor.RED + "Unknown subcommand.");
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,
                                      String alias, String[] args) {
        if (args.length == 1) {
            return filter(List.of("give", "list", "info", "reload"), args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            List<String> names = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                names.add(player.getName());
            }
            return filter(names, args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            return filter(prefixIds(), args[1]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return filter(prefixIds(), args[2]);
        }
        return List.of();
    }

    private List<String> prefixIds() {
        return items().ids().stream().map(id -> "eis:" + id).toList();
    }

    private List<String> filter(List<String> values, String input) {
        String lower = input.toLowerCase();
        return values.stream()
                .filter(value -> value.toLowerCase().startsWith(lower))
                .toList();
    }
}

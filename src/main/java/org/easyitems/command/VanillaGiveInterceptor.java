package org.easyitems.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.easyitems.item.CustomItem;
import org.easyitems.item.ItemManager;

public final class VanillaGiveInterceptor implements Listener {
    private final ItemManager items;

    public VanillaGiveInterceptor(org.easyitems.EasyItemsPlugin plugin, ItemManager items) {
        this.items = items;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String raw = event.getMessage();
        if (raw == null || !raw.startsWith("/")) return;

        String[] args = raw.substring(1).trim().split("\\s+");
        if (args.length < 3) return;

        String command = args[0];
        if (!(command.equalsIgnoreCase("give")
                || command.equalsIgnoreCase("minecraft:give"))) {
            return;
        }

        String itemId = args[2];
        if (!itemId.toLowerCase().startsWith("eis:")) return;

        Player player = event.getPlayer();
        if (!player.hasPermission("easyitems.give")) {
            player.sendMessage(ChatColor.RED
                    + "You don't have permission to give EasyItems.");
            event.setCancelled(true);
            return;
        }

        if (!args[1].equalsIgnoreCase("@s")) {
            player.sendMessage(ChatColor.RED
                    + "MVP: EasyItems /give currently supports @s only.");
            event.setCancelled(true);
            return;
        }

        CustomItem item = items.get(itemId);
        if (item == null) {
            player.sendMessage(ChatColor.RED + "Unknown EasyItems item: " + itemId);
            event.setCancelled(true);
            return;
        }

        int amount = 1;
        if (args.length >= 4) {
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid amount.");
                event.setCancelled(true);
                return;
            }
        }

        if (amount < 1 || amount > 99) {
            player.sendMessage(ChatColor.RED + "Amount must be between 1 and 99.");
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        player.getInventory().addItem(item.create(amount));
        player.sendMessage(ChatColor.GREEN + "Given "
                + amount + "x " + item.getFullId());
    }
}

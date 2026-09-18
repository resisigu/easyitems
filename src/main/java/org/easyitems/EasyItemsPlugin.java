package org.easyitems;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.easyitems.command.EisCommand;
import org.easyitems.command.VanillaGiveInterceptor;
import org.easyitems.item.ItemManager;

public final class EasyItemsPlugin extends JavaPlugin {
    private ItemManager itemManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        itemManager = new ItemManager(this);
        itemManager.reload();

        EisCommand eisCommand = new EisCommand(this);
        PluginCommand command = getCommand("eis");
        if (command != null) {
            command.setExecutor(eisCommand);
            command.setTabCompleter(eisCommand);
        }

        getServer().getPluginManager().registerEvents(
                new VanillaGiveInterceptor(this, itemManager),
                this
        );

        getLogger().info("EasyItems enabled. Loaded " + itemManager.size() + " item(s).");
        getLogger().info("Try: /give @s eis:ruby");
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public void reloadItems() {
        reloadConfig();
        itemManager.reload();
    }
}

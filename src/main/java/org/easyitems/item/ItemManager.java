package org.easyitems.item;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.easyitems.EasyItemsPlugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ItemManager {
    private final EasyItemsPlugin plugin;
    private final Map<String, CustomItem> items = new LinkedHashMap<>();

    public ItemManager(EasyItemsPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        items.clear();

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("items");
        if (section == null) {
            plugin.getLogger().warning("No 'items' section found in config.yml.");
            return;
        }

        for (String rawId : section.getKeys(false)) {
            String id = rawId.toLowerCase(Locale.ROOT);

            if (!id.matches("[a-z0-9._-]+")) {
                plugin.getLogger().warning("Invalid EasyItems ID: " + rawId);
                continue;
            }

            String materialName = section.getString(rawId + ".material", "STONE");
            Material material = Material.matchMaterial(materialName);
            if (material == null || !material.isItem()) {
                plugin.getLogger().warning(
                        "Invalid material for eis:" + id + ": " + materialName
                );
                continue;
            }

            String displayName = section.getString(rawId + ".display-name", id);
            String model = section.getString(rawId + ".model", id);
            List<String> lore = section.getStringList(rawId + ".lore");

            items.put(id, new CustomItem(
                    plugin, id, material, displayName, model, lore
            ));
        }
    }

    public CustomItem get(String id) {
        if (id == null) return null;
        return items.get(normalize(id));
    }

    public boolean contains(String id) {
        return get(id) != null;
    }

    public int size() {
        return items.size();
    }

    public List<String> ids() {
        return new ArrayList<>(items.keySet());
    }

    public String normalize(String input) {
        String id = input.trim().toLowerCase(Locale.ROOT);
        if (id.startsWith("eis:")) {
            id = id.substring(4);
        }
        return id;
    }
}

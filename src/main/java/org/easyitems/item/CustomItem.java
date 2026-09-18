package org.easyitems.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.easyitems.EasyItemsPlugin;

import java.util.ArrayList;
import java.util.List;

public final class CustomItem {
    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacyAmpersand();

    private final EasyItemsPlugin plugin;
    private final String id;
    private final Material material;
    private final String displayName;
    private final String model;
    private final List<String> lore;

    public CustomItem(EasyItemsPlugin plugin, String id, Material material,
                      String displayName, String model, List<String> lore) {
        this.plugin = plugin;
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.model = model;
        this.lore = List.copyOf(lore);
    }

    public String getId() {
        return id;
    }

    public String getFullId() {
        return "eis:" + id;
    }

    public ItemStack create(int amount) {
        ItemStack stack = new ItemStack(material, Math.max(1, Math.min(amount, 99)));

        ItemMeta meta = stack.getItemMeta();
        if (displayName != null && !displayName.isBlank()) {
            meta.displayName(LEGACY.deserialize(displayName));
        }

        if (!lore.isEmpty()) {
            List<net.kyori.adventure.text.Component> components = new ArrayList<>();
            for (String line : lore) {
                components.add(LEGACY.deserialize(line));
            }
            meta.lore(components);
        }

        NamespacedKey itemIdKey = new NamespacedKey(plugin, "item_id");
        meta.getPersistentDataContainer().set(
                itemIdKey,
                PersistentDataType.STRING,
                id
        );

        stack.setItemMeta(meta);

        if (model != null && !model.isBlank()) {
            String modelPath = model;
            if (modelPath.contains(":")) {
                int colon = modelPath.indexOf(':');
                String namespace = modelPath.substring(0, colon);
                String path = modelPath.substring(colon + 1);
                stack.setData(DataComponentTypes.ITEM_MODEL, Key.key(namespace, path));
            } else {
                stack.setData(DataComponentTypes.ITEM_MODEL, Key.key("eis", modelPath));
            }
        }

        return stack;
    }
}

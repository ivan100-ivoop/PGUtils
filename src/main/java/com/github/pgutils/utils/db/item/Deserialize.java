package com.github.pgutils.utils.db.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.pgutils.PGUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Deserialize extends JsonDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode itemNode = p.getCodec().readTree(p);

        Material type = Material.getMaterial(itemNode.get("type").asText());
        if (type == null) {
            PGUtils.logger.log(Level.SEVERE, "Invalid material type: " + itemNode.get("type").asText());
            return null;
        }

        ItemStack item = new ItemStack(type);
        item.setAmount(itemNode.get("amount").asInt());

        if (itemNode.has("displayName")) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(itemNode.get("displayName").asText());
            item.setItemMeta(meta);
        }

        if (itemNode.has("lore")) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            for (JsonNode loreNode : itemNode.get("lore")) {
                lore.add(loreNode.asText());
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        if (itemNode.has("enchants") && itemNode.get("enchants").isObject()) {
            ItemMeta meta = item.getItemMeta();
            JsonNode enchantsNode = itemNode.get("enchants");
            enchantsNode.fields().forEachRemaining(entry -> {
                String enchantName = entry.getKey();
                int level = entry.getValue().asInt();
                Enchantment enchantment = Enchantment.getByKey(new NamespacedKey(PGUtils.instance, enchantName));
                if (enchantment != null) {
                    meta.addEnchant(enchantment, level, true);
                } else {
                    PGUtils.logger.log(Level.SEVERE, "Enchantment not found: " + enchantName);
                }
            });
            item.setItemMeta(meta);
        }

        return item;
    }
}

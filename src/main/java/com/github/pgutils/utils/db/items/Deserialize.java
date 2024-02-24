package com.github.pgutils.utils.db.items;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Deserialize extends JsonDeserializer<List<ItemStack>> {
    @Override
    public List<ItemStack> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        List<ItemStack> items = new ArrayList<>();
        for (JsonNode itemNode : node) {
            ItemStack item = new ItemStack(Material.valueOf(itemNode.get("type").asText()));
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
                Iterator<Map.Entry<String, JsonNode>> entry = enchantsNode.fields();
                while (entry.hasNext()) {
                    Map.Entry<String, JsonNode> enchantments = entry.next();
                    String enchant = (enchantments.getKey().equalsIgnoreCase("unbreaking") ? "DURABILITY" : enchantments.getKey());
                    Enchantment enchantment = Enchantment.getByKey(new NamespacedKey(PGUtils.instance, enchant));
                    if (enchantment != null) {
                        meta.addEnchant(enchantment, enchantments.getValue().asInt(), true);
                    } else {
                        PGUtils.logger.log(Level.SEVERE, String.format("Enchantment not found: %s", enchantments.getKey()));
                    }
                }
                item.setItemMeta(meta);
            }


            items.add(item);
        }
        return items;
    }
}

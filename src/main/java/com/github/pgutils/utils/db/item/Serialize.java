package com.github.pgutils.utils.db.item;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.Map;

public class Serialize extends JsonSerializer<ItemStack> {

    @Override
    public void serialize(ItemStack item, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        gen.writeStartObject();
        gen.writeStringField("type", item.getType().toString());
        gen.writeNumberField("amount", item.getAmount());
        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            gen.writeStringField("displayName", meta.getDisplayName());
        }

        if (meta != null && meta.hasLore()) {
            gen.writeFieldName("lore");
            gen.writeStartArray();
            for (String lore : meta.getLore()) {
                gen.writeString(lore);
            }
            gen.writeEndArray();
        }

        if (meta != null && meta.hasEnchants()) {
            gen.writeFieldName("enchants");
            gen.writeStartObject();
            for (Map.Entry<Enchantment, Integer> enchant : meta.getEnchants().entrySet()) {
                String enchantKey = enchant.getKey().getKey().getKey();
                if (enchantKey.equalsIgnoreCase("unbreaking")) {
                    enchantKey = "minecraft:durability";
                }
                gen.writeNumberField(enchantKey, enchant.getValue());
            }
            gen.writeEndObject();
        }

        gen.writeEndObject();
    }
}

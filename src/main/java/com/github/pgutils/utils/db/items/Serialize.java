package com.github.pgutils.utils.db.items;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Serialize extends JsonSerializer<List<ItemStack>> {
    @Override
    public void serialize(List<ItemStack> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for (ItemStack item : value){
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
                    String enchants = (enchant.getKey().getKey().getKey().equalsIgnoreCase("unbreaking") ? "DURABILITY" : enchant.getKey().getKey().getKey());
                    gen.writeFieldName(enchants);
                    gen.writeNumber(enchant.getValue());
                }
                gen.writeEndObject();
            }

            gen.writeEndObject();
        }
        gen.writeEndArray();
    }
}

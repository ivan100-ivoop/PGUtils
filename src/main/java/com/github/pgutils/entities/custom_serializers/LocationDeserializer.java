package com.github.pgutils.entities.custom_serializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;

public class LocationDeserializer extends JsonDeserializer<Location> {
    @Override
    public Location deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String worldName = node.get("worldName").asText();
        double x = node.get("x").asDouble();
        double y = node.get("y").asDouble();
        double z = node.get("z").asDouble();
        float yaw = (float) node.get("yaw").asDouble();
        float pitch = (float) node.get("pitch").asDouble();
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }
}


package com.github.pgutils.utils.db.location;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.Location;

import java.io.IOException;

public class Serialize extends JsonSerializer<Location> {

    @Override
    public void serialize(Location location, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("world", location.getWorld().getName());
        jsonGenerator.writeNumberField("x", location.getX());
        jsonGenerator.writeNumberField("y", location.getY());
        jsonGenerator.writeNumberField("z", location.getZ());
        jsonGenerator.writeNumberField("yaw", (double) location.getYaw());
        jsonGenerator.writeNumberField("pitch", (double) location.getPitch());
        jsonGenerator.writeEndObject();
    }
}

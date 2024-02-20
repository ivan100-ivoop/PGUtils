package com.github.pgutils.entities.custom_serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.Location;

import java.io.IOException;

public class LocationSerializer extends JsonSerializer<Location> {

    @Override
    public void serialize(Location location, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("worldName", location.getWorld().getName());
        jsonGenerator.writeNumberField("x", location.getX());
        jsonGenerator.writeNumberField("y", location.getY());
        jsonGenerator.writeNumberField("z", location.getZ());
        jsonGenerator.writeNumberField("yaw", (double) location.getYaw());
        jsonGenerator.writeNumberField("pitch", (double) location.getPitch());
        jsonGenerator.writeEndObject();
    }
}
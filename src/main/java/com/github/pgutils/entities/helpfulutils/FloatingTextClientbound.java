package com.github.pgutils.entities.helpfulutils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.sun.org.apache.xml.internal.utils.res.IntArrayWrapper;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class FloatingTextClientbound {
    private Player player;
    private Location location;
    private String text;
    private int entityId;
    private UUID entityUUID;

    public FloatingTextClientbound(Player player, Location location, String text) {
        this.player = player;
        this.location = location;
        this.text = text;
        this.entityId = generateEntityId();
        this.entityUUID = UUID.randomUUID();
        spawn();
    }


    private void spawn() {
        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        spawnPacket.getIntegers().write(0, entityId);
        spawnPacket.getUUIDs().write(0, entityUUID);
        spawnPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        spawnPacket.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());


        sendPacket(spawnPacket);

        PacketContainer metadataPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metadataPacket.getIntegers().write(0, entityId);

        WrappedDataWatcher metadata = new WrappedDataWatcher();

        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
        Optional<WrappedChatComponent> chatComponent = Optional.of(WrappedChatComponent.fromText(text));
        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x20);

        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, serializer), chatComponent);

        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);

        final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();

        for(final WrappedWatchableObject entry : metadata.getWatchableObjects()) {
            if(entry == null) continue;

            final WrappedDataWatcher.WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
            wrappedDataValueList.add(
                    new WrappedDataValue(
                            watcherObject.getIndex(),
                            watcherObject.getSerializer(),
                            entry.getRawValue()
                    )
            );
        }

        metadataPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);

        sendPacket(metadataPacket);



    }

    public void updateText(String newText) {
        this.text = newText;
        PacketContainer metadataPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metadataPacket.getIntegers().write(0, entityId);

        WrappedDataWatcher metadata = new WrappedDataWatcher();

        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.getChatComponentSerializer(true);

        Optional<WrappedChatComponent> chatComponent = Optional.of(WrappedChatComponent.fromText(newText));

        metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, serializer), chatComponent);

        final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();

        for(final WrappedWatchableObject entry : metadata.getWatchableObjects()) {
            if(entry == null) continue;

            final WrappedDataWatcher.WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
            wrappedDataValueList.add(
                    new WrappedDataValue(
                            watcherObject.getIndex(),
                            watcherObject.getSerializer(),
                            entry.getRawValue()
                    )
            );
        }

        metadataPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);

        sendPacket(metadataPacket);
    }

    public void remove() {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, Arrays.asList(entityId));

        sendPacket(destroyPacket);
    }

    private void sendPacket(PacketContainer packet) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    private int generateEntityId() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }
}
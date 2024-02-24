package com.github.pgutils.entities.helpfulutils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.github.pgutils.utils.GeneralUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;


public class ClientboundArmorstand {
    private Player player;
    private Location location;
    private String text;
    private int entityId;
    private UUID entityUUID;

    private boolean attachedToPlayer;

    private Vector offset;

    public ClientboundArmorstand(Player player, Location location, String text) {
        this.player = player;
        this.location = location;
        this.text = text;
        this.entityId = GeneralUtils.generateEntityId();
        this.entityUUID = UUID.randomUUID();
        spawn();
    }

    public ClientboundArmorstand(Player player, Location location, String text, Vector offset) {
        this.player = player;
        this.location = location;
        this.text = text;
        this.entityId = GeneralUtils.generateEntityId();
        this.entityUUID = UUID.randomUUID();
        this.offset = offset;
        attachedToPlayer = true;
        spawn();
    }


    private void spawn() {
        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        spawnPacket.getIntegers().write(0, entityId);
        spawnPacket.getUUIDs().write(0, entityUUID);
        spawnPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        if (!attachedToPlayer) {
            spawnPacket.getDoubles()
                    .write(0, location.getX())
                    .write(1, location.getY())
                    .write(2, location.getZ());
        } else {
            spawnPacket.getDoubles()
                    .write(0, player.getLocation().getX() + offset.getX())
                    .write(1, player.getLocation().getY() + offset.getY())
                    .write(2, player.getLocation().getZ() + offset.getZ());
            spawnPacket.getBytes().write(1, (byte)(((location.getPitch() * 256.0F) / 360.0F)));
        }

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

        PacketContainer enforceRotation2 = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_LOOK);
        enforceRotation2.getIntegers().writeSafely(0, entityId);
        enforceRotation2.getBytes().writeSafely(0, (byte) (location.getYaw()*256F / 360F));
        enforceRotation2.getBytes().writeSafely(1, (byte) (location.getPitch()*256F / 360F));
        sendPacket(enforceRotation2);

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

    public void updateLocation(Location location) {
        this.location = location;
        if (attachedToPlayer){
            location = player.getLocation().clone().add(offset);
        }

        PacketContainer teleportPacket = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        teleportPacket.getIntegers().write(0, entityId);
        teleportPacket.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());

        sendPacket(teleportPacket);
        if (attachedToPlayer) {
            PacketContainer enforceRotation2 = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_LOOK);
            enforceRotation2.getIntegers().writeSafely(0, entityId);
            enforceRotation2.getBytes().writeSafely(0, (byte) (location.getYaw() * 256F / 360F));
            enforceRotation2.getBytes().writeSafely(1, (byte) (location.getPitch() * 256F / 360F));
            sendPacket(enforceRotation2);
        }
    }

    public void updateEquipment(String slot, ItemStack item) {
        PacketContainer equipmentPacket = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equipmentPacket.getIntegers().write(0, entityId);
        equipmentPacket.getSlotStackPairLists().write(0, Arrays.asList(new Pair<>(EnumWrappers.ItemSlot.valueOf(slot), item)));
        sendPacket(equipmentPacket);
    }

    public void remove() {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, Arrays.asList(entityId));
        sendPacket(destroyPacket);
    }

    private void sendPacket(PacketContainer packet) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }


    public void setOffset(Vector setY) {
        this.offset = setY;
    }
}
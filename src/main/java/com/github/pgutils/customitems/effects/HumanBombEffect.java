package com.github.pgutils.customitems.effects;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.github.pgutils.PGUtilsLoader;
import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.customitems.CustomItemRepository;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Keys;
import com.github.pgutils.utils.PlayerManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class HumanBombEffect extends CustomEffect {

    private int bombTicks;

    private boolean blewUp = false;

    private int blindnessTicks = 60;

    private int speedTicks = 100;

    private int leeway = 300;

    private int speedLevel = 0;

    private int speedLevelMax = 5;

    private List<Player> revealedPlayers;

    private Player effectedPlayer;
    public HumanBombEffect(Entity effectedEntity, int bombTicks) {
        super(effectedEntity);

        revealedPlayers = new ArrayList<>();
        if (!(effectedEntity instanceof Player)){
            CustomEffect.removeEffect(this);
            return;
        }
        effectedPlayer = (Player) effectedEntity;
        this.bombTicks = bombTicks;
        // Add a tnt on their head
        ItemStack tnt = new ItemStack(Material.TNT);
        ItemMeta meta = tnt.getItemMeta();
        meta.setDisplayName("§c§lBombhead");
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        tnt.setItemMeta(meta);
        effectedPlayer.getInventory().setHelmet(tnt);
        // Give whole red set of leather armor

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemMeta meta2 = chestplate.getItemMeta();
        meta2.setDisplayName("§c§lBomb Vest");
        meta2.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta2.setUnbreakable(true);
        meta2.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) meta2;
        leatherArmorMeta.setColor(Color.RED);
        chestplate.setItemMeta(meta2);
        effectedPlayer.getInventory().setChestplate(chestplate);


        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemMeta meta3 = leggings.getItemMeta();
        meta3.setDisplayName("§c§lBomb Pants");
        meta3.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta3.setUnbreakable(true);
        meta3.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        leatherArmorMeta = (LeatherArmorMeta) meta3;
        leatherArmorMeta.setColor(Color.RED);
        leggings.setItemMeta(meta3);
        effectedPlayer.getInventory().setLeggings(leggings);


        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        ItemMeta meta4 = boots.getItemMeta();
        meta4.setDisplayName("§c§lBomb Boots");
        meta4.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta4.setUnbreakable(true);
        meta4.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        leatherArmorMeta = (LeatherArmorMeta) meta4;
        leatherArmorMeta.setColor(Color.RED);
        boots.setItemMeta(meta4);
        effectedPlayer.getInventory().setBoots(boots);

        ItemStack bomb = CustomItemRepository.createBombhead();

        if (bombTicks < leeway) {
            this.bombTicks = leeway;
        }
        effectedPlayer.sendTitle("§c§lYou got the bomb!", "§c§lYou have " + bombTicks/20 + " seconds to pass it!", 10, 70, 20);
        effectedPlayer.getInventory().setItem(0, bomb);
        effectedPlayer.playSound(effectedPlayer.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);

        PlayerManager.disableMove(effectedPlayer);
        effectedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 255, false));
    }

    @Override
    public void onUpdate() {
        bombTicks--;


        if (getTicks() == blindnessTicks) {
            PlayerManager.enableMove(effectedPlayer);
            effectedPlayer.removePotionEffect(PotionEffectType.BLINDNESS);
            PlayerManager.enablePVP(effectedPlayer);
        }

        if (getTicks() % speedTicks == 0 && speedLevel < speedLevelMax) {
            speedLevel++;
        }
        effectedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, speedLevel, true));

        if (getTicks() > 100) {
            revealPlayers();
        }

        // Make the player's head smoke
        Location particleLoc = effectedPlayer.getLocation().clone().add(0, 2, 0);

        effectedPlayer.getWorld().spawnParticle(Particle.SMOKE_NORMAL, particleLoc, 0, 0, 0.03, 0, 0.01);

        ItemStack item = effectedPlayer.getInventory().getItemInMainHand();

        // A sound is played when the bomb has 5 second left to explode
        // It becomes quicker as the time goes down
        if (bombTicks <= 0) {
            effectedPlayer.getInventory().clear();
            effectedPlayer.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION_HUGE, effectedPlayer.getLocation().clone().add(0,1,0), 5, 0.5, 1.5, 0.5);
            effectedPlayer.getWorld().playSound(effectedPlayer.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            effectedPlayer.setGameMode(GameMode.SPECTATOR);
            blewUp = true;
            CustomEffect.removeEffect(this);
            new LostBombEffect(effectedPlayer);
            return;
        }

        if (item == null)
            return;
        if (item.getItemMeta() == null)
            return;
        if (item.getItemMeta().getPersistentDataContainer() == null)
            return;

        if (item.getItemMeta().getPersistentDataContainer().has(Keys.bombHead, PersistentDataType.BOOLEAN)) {
            int timeleft = 1+bombTicks/20;
            effectedPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&c" + timeleft)));
            if (bombTicks == 1)
                effectedPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&c§lBOOM!")));
        }
    }

    @Override
    public void onRemove() {
        // Remove the tnt from the players head
        // Give the player full set of white leather armor

        effectedPlayer.getInventory().clear();

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemMeta meta = helmet.getItemMeta();
        meta.setDisplayName("§f§lParty Hat");
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) meta;
        leatherArmorMeta.setColor(Color.WHITE);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setUnbreakable(true);
        helmet.setItemMeta(meta);
        effectedPlayer.getInventory().setHelmet(helmet);

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemMeta meta2 = chestplate.getItemMeta();
        meta2.setDisplayName("§f§lParty Vest");
        meta2.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta2.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        leatherArmorMeta = (LeatherArmorMeta) meta2;
        leatherArmorMeta.setColor(Color.WHITE);
        meta2.setUnbreakable(true);
        chestplate.setItemMeta(meta2);
        effectedPlayer.getInventory().setChestplate(chestplate);

        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemMeta meta3 = leggings.getItemMeta();
        meta3.setDisplayName("§f§lParty Pants");
        meta3.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta3.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        leatherArmorMeta = (LeatherArmorMeta) meta3;
        leatherArmorMeta.setColor(Color.WHITE);
        meta3.setUnbreakable(true);
        leggings.setItemMeta(meta3);
        effectedPlayer.getInventory().setLeggings(leggings);

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        ItemMeta meta4 = boots.getItemMeta();
        meta4.setDisplayName("§f§lParty Shoes");
        meta4.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta4.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        leatherArmorMeta = (LeatherArmorMeta) meta4;
        leatherArmorMeta.setColor(Color.WHITE);
        meta4.setUnbreakable(true);
        boots.setItemMeta(meta4);
        effectedPlayer.getInventory().setBoots(boots);
        // Remove the bomb from the players inventory

        if (blewUp){
            effectedPlayer.getInventory().clear();
        }
        PlayerManager.disablePVP(effectedPlayer);
        hidePlayers();

    }

    public int getBombTicks() {
        return bombTicks;
    }

    public void revealPlayers() {
        effectedPlayer.getWorld().getNearbyEntities(effectedPlayer.getLocation(), 30, 30, 30).forEach(entity -> {
            if (entity instanceof Player) {
                Player player = (Player) entity;

                if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
                    return;


                PacketContainer playerdata = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
                WrappedDataWatcher metadata = new WrappedDataWatcher();
                playerdata.getIntegers().write(0, player.getEntityId());

                metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x40);

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

                playerdata.getDataValueCollectionModifier().write(0, wrappedDataValueList);

                try {
                    PGUtilsLoader.protocolManager.sendServerPacket(effectedPlayer, playerdata);
                } catch (Exception e) {
                    e.printStackTrace();

                }
                if (revealedPlayers.contains(player))
                    return;
                revealedPlayers.add(player);
            }
        });
    }

    public void hidePlayers() {
       revealedPlayers.forEach(player -> {
                PacketContainer playerdata = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
                WrappedDataWatcher metadata = new WrappedDataWatcher();
                playerdata.getIntegers().write(0, player.getEntityId());

                metadata.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x0);

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

                playerdata.getDataValueCollectionModifier().write(0, wrappedDataValueList);

                try {
                    PGUtilsLoader.protocolManager.sendServerPacket(effectedPlayer, playerdata);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            });
    }

}

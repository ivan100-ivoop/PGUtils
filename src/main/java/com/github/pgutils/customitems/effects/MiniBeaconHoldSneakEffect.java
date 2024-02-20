package com.github.pgutils.customitems.effects;

import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.entities.helpfulutils.ClientboundArmorstand;
import com.github.pgutils.utils.Keys;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class MiniBeaconHoldSneakEffect extends CustomEffect {
    Player effectedPlayer;
    List<ClientboundArmorstand> heads;
    public MiniBeaconHoldSneakEffect(Entity effectedEntity) {
        super(effectedEntity);
        if (!(effectedEntity instanceof Player)){
            CustomEffect.removeEffect(this);
            return;
        }
        this.effectedPlayer = (Player) effectedEntity;

        heads = new ArrayList<>();
        // 3 heads side to side and they should be 1.5 block apart
        heads.add(new ClientboundArmorstand(effectedPlayer, effectedPlayer.getLocation().clone(), "§6§lSpeed", effectedPlayer.getLocation().getDirection().normalize().multiply(1.5).setY(-1)));
        heads.add(new ClientboundArmorstand(effectedPlayer, effectedPlayer.getLocation().clone(), "§6§lHaste", effectedPlayer.getLocation().getDirection().normalize().multiply(1.5).setY(-1)));
        heads.add(new ClientboundArmorstand(effectedPlayer, effectedPlayer.getLocation().clone(), "§6§lRegeneration", effectedPlayer.getLocation().getDirection().normalize().multiply(1.5).setY(-1)));
        for (ClientboundArmorstand head : heads) {
            head.updateEquipment("HEAD", new ItemStack(Material.BEACON, 1));
        }
        System.out.println("Added head");
    }

    @Override
    public void onUpdate() {
        if (!effectedPlayer.isSneaking()){
            CustomEffect.removeEffect(this);
            return;
        }

        ItemStack item = effectedPlayer.getInventory().getItemInMainHand();

        if (item == null) {
            CustomEffect.removeEffect(this);
            return;
        }
        if (item.getItemMeta() == null) {
            CustomEffect.removeEffect(this);
            return;
        }
        if (!item.getItemMeta().getPersistentDataContainer().has(Keys.miniBeacon, PersistentDataType.BOOLEAN)) {
            CustomEffect.removeEffect(this);
            return;
        }

        for (int i = 0; i < heads.size(); i++) {
            ClientboundArmorstand head = heads.get(i);
            Vector Offset = effectedPlayer.getLocation().getDirection().clone().crossProduct(new Vector(0, -1, 0)).normalize().multiply(2);
            Vector InitialOffset = effectedPlayer.getLocation().getDirection().clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(i * 2);
            Vector direction = effectedPlayer.getLocation().getDirection().clone().normalize().multiply(2);
            head.updateLocation(null);
            head.setOffset(direction.add(InitialOffset).add(Offset).setY(-0.5));
        }

    }

    @Override
    public void onRemove() {
        for (ClientboundArmorstand head : heads) {
            head.remove();
        }

    }
}

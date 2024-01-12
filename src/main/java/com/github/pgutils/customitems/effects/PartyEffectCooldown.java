package com.github.pgutils.customitems.effects;

import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Keys;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class PartyEffectCooldown extends CustomEffect {

    int maxTicks = 300;

    public PartyEffectCooldown(Player effectedPlayer) {
        super(effectedPlayer);
    }

    @Override
    public void onUpdate() {
        ItemStack item = getEffectedPlayer().getInventory().getItemInMainHand();
        if (getTicks() > maxTicks){
            CustomEffect.removeEffect(this);
        }
        if (item == null)
            return;
        if (item.getItemMeta() == null)
            return;

        if (item.getItemMeta().getPersistentDataContainer().get(Keys.partyStick, PersistentDataType.BOOLEAN)) {
            int percentage = (int) ((double) getTicks() / (double) maxTicks * 100);
            getEffectedPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&cParty Cooldown %cooldownbar%")
                    .replace("%cooldownbar%", GeneralUtils.generateLoadingBar(percentage, "§e", "§7"))));
            if (percentage == 100) {
                getEffectedPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&aParty Cooldown Ready!")));
                getEffectedPlayer().playSound(getEffectedPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            }
        }

    }
}

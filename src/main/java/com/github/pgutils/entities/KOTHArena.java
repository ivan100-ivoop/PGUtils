package com.github.pgutils.entities;

import com.github.pgutils.GeneralUtils;
import com.github.pgutils.interfaces.EvenDependent;

import com.github.pgutils.enums.GameStatus;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.List;

public class KOTHArena extends PlaySpace implements EvenDependent {

    List<Location> spawns;

    List<KOTHPoint> points;

    private int startingTime = 60;

    private int startingTick = 0;

    private int testMessageTime = 100;

    private int testMessageTick = 0;

    public KOTHArena() {
        super();
    }

    @Override
    public void start() {
        for (Player player : players) {
            player.teleport(spawns.get((int) (Math.random() * spawns.size())));
        }
    }

    public void onUpdate() {
        if (status == GameStatus.STARTING) {
            startingTick++;
            if (startingTick % 20 == 0 && startingTick != 0 && startingTick != startingTime) {
                players.forEach(player -> player.sendTitle((startingTime / 20 - startingTick / 20) + "", "", 0, 20, 0));
            }
            else if (startingTick >= startingTime) {
                status = GameStatus.IN_PROGRESS;
                players.forEach(player -> player.sendTitle("GO!", "", 0, 20, 0));
            }

        } else if (status == GameStatus.IN_PROGRESS) {
            testMessageTick++;
            if (testMessageTick >= testMessageTime) {
                testMessageTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&eGame is in progress!"))));
            }
            if (tick % 100 == 0) {
                end();
            }
        }

    }

    @Override
    public void endProcedure() {
        players.stream()
                .forEach(player -> player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD, 1)));
    }
}

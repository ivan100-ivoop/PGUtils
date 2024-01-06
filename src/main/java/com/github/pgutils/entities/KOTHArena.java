package com.github.pgutils.entities;

import com.github.pgutils.GeneralUtils;
import com.github.pgutils.interfaces.EvenDependent;

import com.github.pgutils.enums.GameStatus;
import com.github.pgutils.interfaces.EvenIndependent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KOTHArena extends PlaySpace implements EvenIndependent {

    List<Location> spawns = new ArrayList<>();

    List<KOTHPoint> points = new ArrayList<>();

    private int startingTime = 60;

    private int startingTick = -1;

    private int testMessageTime = 10;

    private int testMessageTick = 0;

    public KOTHArena() {
        super();
        type = "KOTH";
    }

    @Override
    public void start() {
        System.out.println("Starting game " + getID() + " of type " + getType() + " with " + players.size() + " players!");
        for (Player player : players) {
            player.teleport(spawns.get((int) (Math.random() * spawns.size())));
        }
    }

    @Override
    public void onUpdate() {
        System.out.printf("Game %d of type %s is in status %s %d\n", getID(), getType(), status.toString(), tick);
        if (status == GameStatus.STARTING) {
            if (tick > 30)
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
            if (tick >= 300) {
                end();
            }
        }
    }

    @Override
    public void endProcedure() {
        startingTick = 0;
        testMessageTick = 0;
        players.stream()
                .forEach(player -> player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD, 1)));
    }

    @Override
    public void setPos(Location pos) {
        super.setPos(pos);
    }

    public int addSpawnLocation(Location location) {
        spawns.add(location);
        return spawns.size() - 1;
    }

    public int removeSpawnLocation(int id) {
        spawns.remove(id);
        return id;
    }

}

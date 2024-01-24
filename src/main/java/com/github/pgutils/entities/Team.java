package com.github.pgutils.entities;

import com.github.pgutils.entities.games.KOTHArena;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Messages;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Team {
    public static final List<String> colors = Arrays.asList("000000", "0000AA", "00AA00", "00AAAA", "AA0000", "AA00AA", "FFAA00", "AAAAAA", "555555", "5555FF", "55FF55", "55FFFF", "FF5555", "FF55FF", "FFFF55");
    private PlaySpace placespace;
    private List<Player> players = new ArrayList<>();
    private Color color;
    private String colorString;
    private int points = 0;
    private org.bukkit.scoreboard.Team team;
    private int id;

    public Team(String color, int id, KOTHArena arena) {
        this.color = Color.fromRGB(Integer.parseInt(color.substring(0), 16));
        this.colorString = "#"+color;
        this.id = id;
        this.placespace = arena;
        team = placespace.getScoreboard().registerNewTeam("Team_" + id);
        team.setAllowFriendlyFire(false);
        placespace.getSbManager().addTeam(placespace.getID(), id, colorString);

    }
    public void addPlayer(Player player) {
        players.add(player);
        team.addEntry(player.getName());
        placespace.getSbManager().addPlayer(placespace.getID(), id, player);
        System.out.println("Added player " + player.getName() + " to team " + id);
        player.sendMessage(Messages.messageWithPrefix("game-join-team", "%color%Joined team %id%!").replace("%color%", GeneralUtils.hexToMinecraftColor(colorString)).replace("%id%", id+""));
    }

    public void removePlayer(Player player) {
        players.remove(player);
        player.getInventory().clear();
        team.removeEntry(player.getName());
    }
    public void addPoint(int point) {
        points += point;
        placespace.getSbManager().setTeamPoint(id, points, placespace.getID());
    }
    public void removePoint(int point) {
        points -= point;
        placespace.getSbManager().setTeamPoint(id, points, placespace.getID());
    }
    public int getPoints() {
        return points;
    }
    public Color getColor() {
        return color;
    }

    public String getColorString() {
        return colorString;
    }
    public int getID() {
        return id;
    }

    public void deleteTeam() {
        for (int i = players.size() - 1; i >= 0; i--) {
            removePlayer(players.get(i));
        }
        team.unregister();
    }


    public List<Player> getPlayers() {
        return players;
    }
}
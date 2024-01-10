package com.github.pgutils.entities.games;

import java.util.*;

import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.entities.games.kothadditionals.KOTHPoint;
import com.github.pgutils.entities.games.kothadditionals.KOTHSpawn;
import com.github.pgutils.entities.games.kothadditionals.KOTHTeam;
import com.github.pgutils.hooks.PGLobbyHook;
import com.github.pgutils.utils.GeneralUtils;

import com.github.pgutils.enums.GameStatus;
import com.github.pgutils.interfaces.EvenIndependent;
import com.github.pgutils.utils.PlayerPVP;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.util.List;
import java.util.stream.Collectors;

public class KOTHArena extends PlaySpace implements EvenIndependent {
    // Convert all ChatColor colors to Color

    List<KOTHSpawn> spawns = new ArrayList<>();

    List<KOTHPoint> points = new ArrayList<>();

    private int startingTime = 60;

    private int startingTick = 0;

    private int testMessageTime = 10;

    private int testMessageTick = 0;

    private int endingTime = 100;

    private int endingTick = 0;

    private List<KOTHTeam> teams = new ArrayList<>();

    // Saved
    private int teamsAmount = 2;

    private ScoreboardManager manager;

    private Scoreboard board;

    private Objective objective;

    // Saved
    private int matchTime = 100;

    private Score scoreTime;

    private boolean overtime = false;

    private int overtimeMAX = 100;

    // Saved
    private int initial_points_active = 2;

    private KOTHTeam winner;

    public KOTHArena() {
        super();
        type = "KOTH";
    }

    @Override
    public void start() {
        System.out.println("Starting game " + getID() + " of type " + getType() + " with " + players.size() + " players!");

        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        objective = board.registerNewObjective("kothScore"+getID(), "dummy", "Team Scores : ");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);


        scoreTime = objective.getScore(GeneralUtils.fixColors("&fTime Left : "));
        scoreTime.setScore(matchTime / 20 / 60);

        Collections.shuffle(players);

        List<String> availableColors = new ArrayList<>(KOTHTeam.colors);
        for (int i = 0; i < teamsAmount; i++) {
            String color = availableColors.get((int) (Math.random() * availableColors.size()));
            availableColors.remove(color);
            teams.add(new KOTHTeam(color, i + 1, this));
        }

        for (int i = 0; i < players.size(); i++) {
            players.get(i).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 100, true, true));
            players.get(i).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 100, true, true));
            teams.get(i % teamsAmount).addPlayer(players.get(i));
        }

        for (int i = 0; i < initial_points_active; i++) {
            activateRandomPoint();
        }

        for (KOTHTeam team : teams) {
            List<KOTHSpawn> teamSpawns = spawns.stream().filter(spawn -> spawn.getTeamID() == team.getID()).collect(Collectors.toList());
            for (Player player : team.getPlayers()) {
                KOTHSpawn spawn = teamSpawns.get((int) (Math.random() * teamSpawns.size()));
                player.teleport(spawn.getPos());
            }
        }
    }

    @Override
    public void onUpdate() {
        if (status == GameStatus.STARTING) {
            if (startingTick % 20 == 0 && startingTick != startingTime) {
                players.forEach(player -> {
                    player.sendTitle((startingTime / 20 - startingTick / 20) + "", "", 0, 20, 0);
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, (startingTime / 20 - startingTick / 20) + 1, 1);
                });
            }
            else if (startingTick >= startingTime) {
                status = GameStatus.IN_PROGRESS;
                players.forEach(player -> {
                    player.sendTitle("GO!", "", 0, 20, 0);
                    player.removePotionEffect(PotionEffectType.SLOW);
                    player.removePotionEffect(PotionEffectType.JUMP);
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
                    PlayerPVP.enablePVP(player);
                });
            }
            startingTick++;

        } else if (status == GameStatus.IN_PROGRESS) {
            testMessageTick++;
            if (testMessageTick >= testMessageTime) {
                testMessageTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&eGame is in progress!"))));
            }
            if (matchTime % 20 == 0 && (matchTime / 20 - tick / 20) >= 0) {
                scoreTime.setScore(matchTime / 20 - tick / 20);
            }
            if (tick - 30 >= matchTime) {
                checkEnd();
            }
            if (tick - 28 == matchTime) {
                players.forEach(player -> {
                    player.sendTitle("§4OVERTIME!", "", 0, 40, 0);
                    overtime = true;
                    player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                });
            }
            if (overtime && tick - 30 >= matchTime + overtimeMAX) {
                end();
            }
            points.stream().forEach(point -> point.update());
        } else if (status == GameStatus.IS_ENDING) {
            endingTick++;
            if (endingTick >= endingTime) {
                end();
            }
        }
    }

    @Override
    public void endProcedure() {
        teams.stream().forEach(team -> team.deleteTeam());
        points.stream().forEach(point -> point.deactivatePoint());
        teams.clear();
        startingTick = 0;
        testMessageTick = 0;
        endingTick = 0;
        if (objective != null) {
            objective.unregister();
            objective = null;
        }
        if (board != null) {
            board.clearSlot(DisplaySlot.SIDEBAR);
            board = null;
        }

    }

    @Override
    public void setPos(Location pos) {
        super.setPos(pos);
    }

    @Override
    public void removePlayer(Player player) {
        players.remove(player);
        teams.stream().forEach(team -> team.removePlayer(player));
    }

    @Override
    public boolean passesChecks() {
        return true;
    }

    @Override
    public void updateView(Player player) {

    }

    public void checkEnd() {
        // Check if there are 2 or more teams with same amount of points
        List<KOTHTeam> teamsWithMostPoints = teams.stream().filter(team -> team.getPoints() == teams.stream().mapToInt(KOTHTeam::getPoints).max().getAsInt()).collect(Collectors.toList());
        // If there is only one team with most points, end the game
        if (teamsWithMostPoints.size() == 1) {
            winner = teamsWithMostPoints.get(0);
            isEnding();
        }

    }

    public void isEnding() {
        status = GameStatus.IS_ENDING;
        players.forEach(player -> {
            player.sendTitle(GeneralUtils.fixColors(KOTHTeam.colorGarbage.get(KOTHTeam.colors.indexOf(winner.getColorString()))+"Team " + winner.getID() + " won!"), "", 0, endingTime, 0);
            player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            PlayerPVP.disablePVP(player);
        });
    }

    public KOTHSpawn addSpawnLocation(Location location, int team_id) {
        spawns.add(new KOTHSpawn(location, team_id, this));
        return spawns.get(spawns.size() - 1);
    }

    public int removeSpawnLocation(int id) {
        spawns.remove(id);
        return id;
    }

    public void addCapturePoint(KOTHPoint point) {
        points.add(point);
    }

    public KOTHPoint addCapturePoint(Location location) {
        KOTHPoint kothPoint =  new KOTHPoint(this, location, 2.5);
        points.add(kothPoint);
        return kothPoint;
    }

    public KOTHPoint addCapturePoint(Location location, int radius) {
        KOTHPoint kothPoint = new KOTHPoint(this, location, radius);
        points.add(kothPoint);
        return kothPoint;

    }

    public KOTHPoint addCapturePoint(Location location, int radius, int pointsAwarding) {
        KOTHPoint kothPoint = new KOTHPoint(this, location, radius, pointsAwarding);
        points.add(kothPoint);
        return kothPoint;
    }

    public KOTHPoint addCapturePoint(Location location, int radius, int pointsAwarding, int timeToCapture) {
        KOTHPoint kothPoint = new KOTHPoint(this, location, radius, pointsAwarding, timeToCapture);
        points.add(kothPoint);
        return kothPoint;
    }

    public void activateRandomPoint() {
        points.stream().forEach(point -> point.tickDown());
        List<KOTHPoint> availablePoints = points.stream().filter(point -> point.isActivitable()).collect(Collectors.toList());
        if (availablePoints.size() == 0) return;
        KOTHPoint point = availablePoints.get((int) (Math.random() * availablePoints.size()));
        point.startActivatingPoint();
    }

    public List<KOTHPoint> getPoints() {
        return points;
    }

    public List<KOTHSpawn> getSpawns() {
        return spawns;
    }

    public List<Player> getPlayers() {
        return players;
    }


    public ScoreboardManager getManager() {
        return manager;
    }

    public List<KOTHTeam> getTeams() {
        return teams;
    }

    public Scoreboard getBoard() {
        return board;
    }

    public Objective getObjective() {
        return objective;
    }

    public Score getScoreTime() {
        return scoreTime;
    }

    public void addSpawn(KOTHSpawn spawn) {
        spawns.add(spawn);
    }

    public int getTeamsAmount() {
        return teamsAmount;
    }

    public void setTeamsAmount(int readObject) {
        this.teamsAmount = readObject;
    }
}

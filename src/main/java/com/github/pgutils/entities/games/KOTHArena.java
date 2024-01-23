package com.github.pgutils.entities.games;

import java.util.*;

import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.entities.games.kothadditionals.KOTHPoint;
import com.github.pgutils.entities.games.kothadditionals.KOTHSpawn;
import com.github.pgutils.entities.games.kothadditionals.KOTHTeam;

import com.github.pgutils.enums.GameStatus;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PlayerManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class KOTHArena extends PlaySpace {

    static {
        playSpaceTypes.put("koth", KOTHArena.class);
    }

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

    // Saved
    private int matchTime = 3000;

    private boolean overtime = false;

    private int overtimeMAX = 1000;

    // Saved
    private int initial_points_active = 2;

    private KOTHTeam winner;

    public KOTHArena() {
        super();
        type = "KOTH";
        commandMap.put("teams-amount", this::setTeamsAmount);
        commandMap.put("match-time", this::setMatchTime);
        commandMap.put("initial-points-active", this::setInitialPointsActive);
    }

    @Override
    public void start() {
        System.out.println("Starting game " + getID() + " of type " + getType() + " with " + players.size() + " players!");

        Collections.shuffle(players);

        List<String> availableColors = new ArrayList<>(KOTHTeam.colors);
        for (int i = 0; i < teamsAmount; i++) {
            String color = availableColors.get((int) (Math.random() * availableColors.size()));
            availableColors.remove(color);
            teams.add(new KOTHTeam(color.toLowerCase(), i + 1, this));
        }

        for (int i = 0; i < players.size(); i++) {
            PlayerManager.disableMove(players.get(i));
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
        getSbManager().createGameScoreboard(getID());
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
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
                    PlayerManager.enablePVP(player);
                    PlayerManager.enableMove(player);
                });
            }
            startingTick++;

        } else if (status == GameStatus.IN_PROGRESS) {
            testMessageTick++;
            if (testMessageTick >= testMessageTime) {
                testMessageTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage("game-progress", "&eGame is in progress!", false))));
            }
            if (matchTime % 20 == 0 && (matchTime / 20 - tick / 20) >= 0) {
                getSbManager().setTime(matchTime / 20 - tick / 20, getID());
            }
            if (tick - 30 >= matchTime) {
                checkEnd();
            }
            if (tick - 30 == matchTime + 1) {
                players.forEach(player -> {
                    player.sendTitle(Messages.getMessage("game-overtime", "§4OVERTIME!", false), "", 0, 40, 0);
                    overtime = true;
                    player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                });
            }
            if (overtime && tick - 30 >= matchTime + overtimeMAX) {
                end(null);
            }
            points.stream().forEach(point -> point.update());
        } else if (status == GameStatus.IS_ENDING) {
            endingTick++;
            if (endingTick >= endingTime) {
                List<Player> players = winner.getPlayers();
                end(players);
            }
        }
    }

    @Override
    public void endProcedure() {
        if (teams.size() > 0)
            teams.stream().forEach(team -> team.deleteTeam());
        points.stream().forEach(point -> {
            point.deactivatePointFull();
            point.resetDownTime();
        });
        teams.clear();
        startingTick = 0;
        testMessageTick = 0;
        endingTick = 0;


    }

    @Override
    public void setPos(Location pos) {
        super.setPos(pos);
    }

    @Override
    public void removePlayer(Player player) {
        players.remove(player);
        teams.stream().forEach(team -> team.removePlayer(player));
        getSbManager().removeScoreboard(player);
    }

    @Override
    public String passesChecks() {

        if (points.size() < initial_points_active + 2) {
            return "Not enough points! Currently : " + points.size() + " | Needed : " + (initial_points_active + 2);
        }

        for (int i = 1; i <= teamsAmount; i++) {
            int finalI = i;
            if (spawns.stream().noneMatch(spawn -> spawn.getTeamID() == finalI)) {
                return "No spawn for team " + i;
            }
        }

        if (getLobby() != null) {
            if (getLobby().getPlayers().size() % teamsAmount != 0) {
                return "Players amount must be divisible by teams amount! Currently : " + getLobby().getPlayers().size() + " | Needed : " + (getLobby().getPlayers().size() + (teamsAmount - getLobby().getPlayers().size() % teamsAmount));
            }
        }

        return "All Done";
    }

    @Override
    public void updateView(Player player) {

    }

    @Override
    public boolean addGameObjects(Player player, String[] args) {
        switch (args[2].toLowerCase()) {
            case "spawn":
                return createSpawn(player, args);
            case "point":
                return createPoint(player, args);
        }
        return false;
    }

    @Override
    public boolean removeGameObjects(Player player, String[] args) {
        return false;
    }

    @Override
    public boolean setGameObjects(Player player, String[] args) {
        switch (args[1].toLowerCase()) {
            case "select-closest":
                return selectClosest(player, args);
            case "delete-closest":
                return deleteClosestObject(player, args);
        }

        return false;
    }

    private boolean deleteClosestObject(Player player, String[] args) {
        List<Location> locations = new ArrayList<>();
        locations.addAll(spawns.stream().map(spawn -> spawn.getPos()).collect(Collectors.toList()));
        locations.addAll(points.stream().map(point -> point.getLocation()).collect(Collectors.toList()));
        Location closest = GeneralUtils.getClosestLocation(player.getLocation(), locations);
        if (closest == null) {
            player.sendMessage(Messages.messageWithPrefix("no-closest-object-message", "&c&lOops &cThere are no objects!"));
            return true;
        }

        if (spawns.stream().anyMatch(spawn -> spawn.getPos().equals(closest))) {
            removeSpawnLocation(spawns.stream().filter(spawn -> spawn.getPos().equals(closest)).findFirst().get().getID());
            player.sendMessage(Messages.messageWithPrefix("spawn-deleted-message", "&aSuccessfully deleted spawn!"));
            return true;
        }

        if (points.stream().anyMatch(point -> point.getLocation().equals(closest))) {
            removePoint(points.stream().filter(point -> point.getLocation().equals(closest)).findFirst().get().getID());
            player.sendMessage(Messages.messageWithPrefix("point-deleted-message", "&aSuccessfully deleted point!"));
            return true;
        }

        return false;
    }
    private boolean selectClosest(Player player, String[] args) {
        List<Location> locations = new ArrayList<>();
        locations.addAll(spawns.stream().map(spawn -> spawn.getPos()).collect(Collectors.toList()));
        locations.addAll(points.stream().map(point -> point.getLocation()).collect(Collectors.toList()));
        Location closest = GeneralUtils.getClosestLocation(player.getLocation(), locations);
        System.out.println("Hello");
        if (closest == null) {
            player.sendMessage(Messages.messageWithPrefix("no-closest-object-message", "&c&lOops &cThere are no objects!"));
            return true;
        }

        if (spawns.stream().anyMatch(spawn -> spawn.getPos().equals(closest))) {
            player.sendMessage(Messages.messageWithPrefix("spawn-selected-message", "&a" +
                    "Selected spawn! &6%id% &awith team id : %team_id%").replace("%id%", spawns.stream().filter(spawn -> spawn.getPos().equals(closest)).findFirst().get().getID() + "").replace("%team_id%", spawns.stream().filter(spawn -> spawn.getPos().equals(closest)).findFirst().get().getTeamID() + ""));

            return true;
        }

        if (points.stream().anyMatch(point -> point.getLocation().equals(closest))) {
            player.sendMessage(Messages.messageWithPrefix("point-selected-message", "&a" +
                    "Selected point! &6%id% &awith radius : %radius% and points : %points% and time to capture : %time%").replace("%id%", points.stream().filter(point -> point.getLocation().equals(closest)).findFirst().get().getID() + "").replace("%radius%", points.stream().filter(point -> point.getLocation().equals(closest)).findFirst().get().getRadius() + "").replace("%points%", points.stream().filter(point -> point.getLocation().equals(closest)).findFirst().get().getPointsAwarding() + "").replace("%time%", points.stream().filter(point -> point.getLocation().equals(closest)).findFirst().get().getCaptureTime() + ""));

            return true;
        }

        return false;
    }

    @Override
    public boolean setGameOptions(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return true;
        }
        System.out.println(args.length);
        BiFunction<Player, String[], Boolean> function = getCommandMap().get(args[2].toLowerCase());
        if (function != null) {
            return function.apply(player, args);
        }

        return false;
    }

    public boolean setTeamsAmount(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return true;
        }
        try {
            int teamsAmount = Integer.parseInt(args[3]);
            if (teamsAmount < 2) {
                player.sendMessage(Messages.messageWithPrefix("team-amount-error-message", "&c&lTeams must be above 2!"));
                return true;
            }
            this.teamsAmount = teamsAmount;
            player.sendMessage(Messages.messageWithPrefix("game-option-set-message", "&aSuccessfully set game option! With option : %option% and value : %value%")
                    .replace("%option%", args[2])
                    .replace("%value%", args[3]));
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return true;
        }
    }

    public boolean setMatchTime(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return true;
        }
        try {
            int matchTime = Integer.parseInt(args[3]);
            if (matchTime < 5) {
                player.sendMessage(Messages.messageWithPrefix("match-time-error-message", "&c&lMatch time must be above 5 seconds!"));
                return true;
            }
            this.matchTime = matchTime;
            this.overtimeMAX = matchTime / 3;
            player.sendMessage(Messages.messageWithPrefix("game-option-set-message", "&aSuccessfully set game option! With option : %option% and value : %value%")
                    .replace("%option%", args[2])
                    .replace("%value%", args[3]));
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return true;
        }
    }

    public boolean setInitialPointsActive(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return true;
        }
        try {
            int initial_points_active = Integer.parseInt(args[3]);
            if (initial_points_active < 1) {
                player.sendMessage(Messages.messageWithPrefix("initial-points-active-error-message", "&c&lInitial points active must be above 1!"));
                return true;
            }
            this.initial_points_active = initial_points_active;
            player.sendMessage(Messages.messageWithPrefix("game-option-set-message", "&aSuccessfully set game option! With option : %option% and value : %value%")
                    .replace("%option%", args[2])
                    .replace("%value%", args[3]));
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return true;
        }
    }


    public boolean createPoint(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return true;
        }
        if (args.length == 3) {
            addCapturePoint(player.getLocation(), 2.5);
            player.sendMessage(Messages.messageWithPrefix("point-created-message", ("&aSuccessfully created point! With id : %id% and radius : %radius%").replace("%id%", points.size() + "").replace("%radius%", "2.5")));
            return true;
        }
        if (args.length == 4) {
            try {
                double radius = Double.parseDouble(args[3]);
                addCapturePoint(player.getLocation(), radius);
                player.sendMessage(Messages.messageWithPrefix("point-created-message", ("&aSuccessfully created point! With id : %id% and radius : %radius%").replace("%id%", points.size() + "").replace("%radius%", radius + "")));
                return true;
            } catch (NumberFormatException e) {
                player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
                return true;
            }
        }
        if (args.length == 5) {
            try {
                double radius = Double.parseDouble(args[3]);
                int pointsAwarding = Integer.parseInt(args[4]);
                addCapturePoint(player.getLocation(), radius, pointsAwarding);
                player.sendMessage(Messages.messageWithPrefix("point-created-message", ("&aSuccessfully created point! With id : %id% and radius : %radius% and points : %points%")
                        .replace("%id%", points.size() + "")
                        .replace("%radius%", radius + "")
                        .replace("%points%", pointsAwarding + "")));
                return true;
            } catch (NumberFormatException e) {
                player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
                return true;
            }
        }
        if (args.length == 6) {
            try {
                double radius = Double.parseDouble(args[3]);
                int pointsAwarding = Integer.parseInt(args[4]);
                int timeToCapture = Integer.parseInt(args[5]);
                addCapturePoint(player.getLocation(), radius, pointsAwarding, timeToCapture);
                player.sendMessage(Messages.messageWithPrefix("point-created-message", ("&aSuccessfully created point! With id : %id% and radius : %radius% and points : %points% and time to capture : %time%")
                        .replace("%id%", points.size() + "")
                        .replace("%radius%", radius + "")
                        .replace("%points%", pointsAwarding + "")
                        .replace("%time%", timeToCapture + "")));
                return true;
            } catch (NumberFormatException e) {
                player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
                return true;
            }
        }
        return false;
    }

    public boolean createSpawn(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return true;
        }

        int team_id = Integer.parseInt(args[3]);
        if (team_id < 1 || team_id > teamsAmount) {
            player.sendMessage(Messages.messageWithPrefix("team-amount-error-message", "&c&lTeam id must be between 1 and " + teamsAmount + "!"));
            return true;
        }
        addSpawnLocation(player.getLocation(), team_id);
        player.sendMessage(Messages.messageWithPrefix("spawn-created-message", ("&aSuccessfully created spawn! With id : %id% and team id : %team_id%").replace("%id%", spawns.size() + "").replace("%team_id%", team_id + "")));
        return true;
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
            player.sendTitle(GeneralUtils.fixColors(GeneralUtils.hexToMinecraftColor(winner.getColorString())+"Team " + winner.getID() + " won!"), "", 0, endingTime, 0);
            player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            PlayerManager.disablePVP(player);

        });
    }

    public KOTHSpawn addSpawnLocation(Location location, int team_id) {
        spawns.add(new KOTHSpawn(location, team_id, this));
        return spawns.get(spawns.size() - 1);
    }

    public void removeSpawnLocation(String id) {
        spawns.removeIf(spawn -> spawn.getID().equals(id));
    }

    private void removePoint(String id) {
        points.removeIf(point -> point.getID().equals(id));
    }

    public void addCapturePoint(KOTHPoint point) {
        points.add(point);
    }

    public KOTHPoint addCapturePoint(Location location) {
        KOTHPoint kothPoint =  new KOTHPoint(this, location, 2.5);
        points.add(kothPoint);
        return kothPoint;
    }

    public KOTHPoint addCapturePoint(Location location, double radius) {
        KOTHPoint kothPoint = new KOTHPoint(this, location, radius);
        points.add(kothPoint);
        return kothPoint;

    }

    public KOTHPoint addCapturePoint(Location location, double radius, int pointsAwarding) {
        KOTHPoint kothPoint = new KOTHPoint(this, location, radius, pointsAwarding);
        points.add(kothPoint);
        return kothPoint;
    }

    public KOTHPoint addCapturePoint(Location location, double radius, int pointsAwarding, int timeToCapture) {
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

    public void addSpawn(KOTHSpawn spawn) {
        spawns.add(spawn);
    }

    public int getTeamsAmount() {
        return teamsAmount;
    }

    public void setTeamsAmount(int readObject) {
        this.teamsAmount = readObject;
    }


    public List<KOTHTeam> getTeams() {
        return teams;
    }
}

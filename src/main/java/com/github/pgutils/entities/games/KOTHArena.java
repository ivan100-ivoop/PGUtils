package com.github.pgutils.entities.games;

import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.entities.entity_utils.KOTHArenaUtils;
import com.github.pgutils.entities.games.kothadditionals.KOTHPoint;
import com.github.pgutils.entities.games.kothadditionals.KOTHSpawn;
import com.github.pgutils.entities.games.kothadditionals.KOTHTeam;

import com.github.pgutils.enums.GameStatus;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PlayerManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class KOTHArena extends PlaySpace {
    // Convert all ChatColor colors to Color

    List<KOTHSpawn> spawns ;

    List<KOTHPoint> points ;

    private int startingTick = 0;

    private int endingTime = 200;

    private int endingTick = 0;

    private List<KOTHTeam> teams;

    // Saved
    private int teamsAmount = 2;

    // Saved
    private int matchTime = 3000;

    private boolean overtime = false;

    private int overtimeMAX = 1000;

    // Saved
    private int initial_points_active = 2;

    private KOTHTeam winner;

    private int nameOfGameTime = 30;

    private int teamRecognitionTime = 30;

    private int infoTime = 30;

    private int startingTime = nameOfGameTime + teamRecognitionTime + infoTime + 60;

    public static List<String> addGameObjects = Arrays.asList("point", "spawn");

    public static List<String> setGameObjects = Arrays.asList("select-closest", "delete-closest");

    public KOTHArena() {
        super();
        type = "KOTH";
        getSetMap().put("teams_amount", this::setTeamsAmount);
        getSetMap().put("match_time", this::setMatchTime);
        getSetMap().put("initial_points_active", this::setInitialPointsActive);
        points = new ArrayList<>();
        spawns = new ArrayList<>();
        teams = new ArrayList<>();
    }

    @Override
    public void start() {
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
            if (startingTick == 0) {
                players.forEach(player -> {
                    player.sendTitle(Messages.getMessage("game-koth-name", "§eKing of the Hill", false), "", 0, 20, 0);
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    PlayerManager.disablePVP(player);
                    PlayerManager.disableMove(player);
                });
            }
            else if (startingTick == nameOfGameTime ) {
                teams.forEach(team -> {
                    team.getPlayers().forEach(player -> {
                        player.sendTitle(Messages.getMessage("game-koth-team", GeneralUtils.hexToMinecraftColor(team.getColorString()) + Messages.getMessage("game-koth-team-announce","You are on team ",false)+team.getID(), false), "", 0, 20, 0);
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    });
                });
            }
            else if (startingTick == nameOfGameTime + teamRecognitionTime ) {
                players.forEach(player -> {
                    player.sendTitle(Messages.getMessage("game-koth-info", "§eCapture points", false), Messages.getMessage("game-koth-info-subtitle", "§eSneak to capture", false), 0, 20, 0);
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                });
            }
            else if (startingTick % 20 == (nameOfGameTime + teamRecognitionTime + infoTime) % 20 && startingTick >= nameOfGameTime + teamRecognitionTime + infoTime && startingTick < startingTime) {
                players.forEach(player -> {
                    player.sendTitle((startingTime / 20 - startingTick / 20) + "", "", 0, 20, 0);
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, (startingTime / 20 - startingTick / 20) + 1, 1);
                });
            }
            else if (startingTick == startingTime) {
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

            if (matchTime % 20 == 0 && (matchTime / 20 - tick / 20) >= 0) {
                getSbManager().setTime(matchTime / 20 - tick / 20, getID());
            }
            if (tick - 30 >= matchTime) {
                checkEnd();
            }
            if (tick - 30 == matchTime + 1) {
                players.forEach(player -> {
                    player.sendTitle(Messages.getMessage("game-koth-overtime", "§4OVERTIME!", false), "", 0, 40, 0);
                    player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                });
                overtime = true;
            }
            if (overtime && tick - 30 >= matchTime + overtimeMAX) {
                end(null);
            }
            points.stream().forEach(point -> point.update());
        } else if (status == GameStatus.IS_ENDING) {
                endingTick++;
                if (endingTick >= endingTime) {
                List<Player> players = new ArrayList<>(winner.getPlayers());
                end(players);
            }
        }
    }

    @Override
    public void endProcedure() {
        if (teams != null) {
            if (teams.size() > 0)
                teams.stream().forEach(team -> team.deleteTeam());
        }
        points.stream().forEach(point -> {
            point.deactivatePointFull();
            point.resetDownTime();
        });
        teams.clear();
        startingTick = 0;
        endingTick = 0;
    }

    @Override
    public void deletePlaySpace() {
        KOTHArenaUtils.deleteArena(this.getUID());
    }

    @Override
    public void setPos(Location pos) {
        super.setPos(pos);
        KOTHArenaUtils.updateLocation(this.getUID(), pos);
    }

    @Override
    protected void saveName() {
        KOTHArenaUtils.updateArenas(this.getUID(), "koth", "name", this.getName());
    }

    @Override
    protected void savePos() {
        KOTHArenaUtils.updateLocation(this.getUID(), this.getPos());
    }



    @Override
    public void onRemovePlayer(Player player) {
        teams.stream().forEach(team -> team.removePlayer(player));
        getSbManager().removeScoreboard(player);
    }

    @Override
    public String passesChecks() {

        if (points.size() < initial_points_active + 2) {
            return Messages.getMessage("game-koth-points-amount-error-message", "&c&lThere must be at least %points% points! Currently : %current%",false).replace("%points%", initial_points_active + 2 + "").replace("%current%", points.size() + "");
        }

        for (int i = 1; i <= teamsAmount; i++) {
            int finalI = i;
            if (spawns.stream().noneMatch(spawn -> spawn.getTeamID() == finalI)) {
                return Messages.getMessage("game-koth-spawn-amount-error-message", "&c&lThere must be a spawn for team %id%",false).replace("%id%", i + "");
            }
        }

        if (getLobby() != null) {
            if (getLobby().getPlayers().size() % teamsAmount != 0) {
                return Messages.getMessage("game-koth-players-amount-error-message", "&c&lPlayers amount must be divisible by teams amount! Currently : %current% | Needed : %needed%",false).replace("%current%", getLobby().getPlayers().size() + "").replace("%needed%", (getLobby().getPlayers().size() + (teamsAmount - getLobby().getPlayers().size() % teamsAmount)) + "");
            }
        }

        return "All Done";
    }

    @Override
    public void updateViewGame(Player player) {
        // Spawn particles in special objects to show them
        spawns.stream().forEach(spawn -> player.spawnParticle(Particle.VILLAGER_HAPPY, spawn.getPos(), 1, 0.3, 1, 0.3, 0.01));
        points.stream().forEach(point -> player.spawnParticle(Particle.CRIT_MAGIC, point.getLocation(), 1, 0.3, 1, 0.3, 0.01));

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
            player.sendMessage(Messages.messageWithPrefix("game-koth-no-closest-object-message", "&c&lOops &cThere are no objects!"));
            return true;
        }

        if (spawns.stream().anyMatch(spawn -> spawn.getPos().equals(closest))) {
            removeSpawnLocation(spawns.stream().filter(spawn -> spawn.getPos().equals(closest)).findFirst().get().getID());
            player.sendMessage(Messages.messageWithPrefix("game-koth-spawn-deleted-message", "&aSuccessfully deleted spawn!"));
            return true;
        }

        if (points.stream().anyMatch(point -> point.getLocation().equals(closest))) {
            removePoint(points.stream().filter(point -> point.getLocation().equals(closest)).findFirst().get().getID());
            player.sendMessage(Messages.messageWithPrefix("game-koth-point-deleted-message", "&aSuccessfully deleted point!"));
            return true;
        }

        return false;
    }
    private boolean selectClosest(Player player, String[] args) {
        List<Location> locations = new ArrayList<>();
        locations.addAll(spawns.stream().map(spawn -> spawn.getPos()).collect(Collectors.toList()));
        locations.addAll(points.stream().map(point -> point.getLocation()).collect(Collectors.toList()));
        Location closest = GeneralUtils.getClosestLocation(player.getLocation(), locations);
        if (closest == null) {
            player.sendMessage(Messages.messageWithPrefix("game-koth-no-closest-object-message", "&c&lOops &cThere are no objects!"));
            return true;
        }

        if (spawns.stream().anyMatch(spawn -> spawn.getPos().equals(closest))) {
            player.sendMessage(Messages.messageWithPrefix("game-koth-spawn-selected-message", "&a" +
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
        BiFunction<Player, String[], Boolean> function = getSetMap().get(args[2].toLowerCase());
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
                player.sendMessage(Messages.messageWithPrefix("game-koth-team-amount-error-message", "&c&lTeams must be above 2!"));
                return true;
            }
            this.teamsAmount = teamsAmount;
            player.sendMessage(Messages.messageWithPrefix("game-option-set-message", "&aSuccessfully set game option! With option : %option% and value : %value%")
                    .replace("%option%", args[2])
                    .replace("%value%", args[3]));
            KOTHArenaUtils.updateArenas(this.getUID(), "koth", "teams_amount", this.teamsAmount);
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
                player.sendMessage(Messages.messageWithPrefix("game-koth-match-time-error-message", "&c&lMatch time must be above 5 seconds!"));
                return true;
            }
            this.matchTime = matchTime;
            this.overtimeMAX = matchTime / 3;
            KOTHArenaUtils.updateArenas(this.getUID(), "koth", "match_time", this.matchTime);
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
                player.sendMessage(Messages.messageWithPrefix("game-koth-initial-points-active-error-message", "&c&lInitial points active must be above 1!"));
                return true;
            }
            this.initial_points_active = initial_points_active;
            player.sendMessage(Messages.messageWithPrefix("game-option-set-message", "&aSuccessfully set game option! With option : %option% and value : %value%")
                    .replace("%option%", args[2])
                    .replace("%value%", args[3]));
            KOTHArenaUtils.updateArenas(this.getUID(), "koth", "initial_points_active", this.initial_points_active);
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
            KOTHArenaUtils.savePoint(addCapturePoint(player.getLocation(), 2.5), this.getUID());
            player.sendMessage(Messages.messageWithPrefix("game-koth-point-created-message", ("&aSuccessfully created point! With id : %id% and radius : %radius%").replace("%id%", points.size() + "").replace("%radius%", "2.5")));
            return true;
        }
        if (args.length == 4) {
            try {
                double radius = Double.parseDouble(args[3]);
                KOTHArenaUtils.savePoint(addCapturePoint(player.getLocation(), radius), this.getUID());
                player.sendMessage(Messages.messageWithPrefix("game-koth-point-created-message", ("&aSuccessfully created point! With id : %id% and radius : %radius%").replace("%id%", points.size() + "").replace("%radius%", radius + "")));
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
                KOTHArenaUtils.savePoint(addCapturePoint(player.getLocation(), radius, pointsAwarding), this.getUID());
                player.sendMessage(Messages.messageWithPrefix("game-koth-point-created-message", ("&aSuccessfully created point! With id : %id% and radius : %radius% and points : %points%")
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
                KOTHArenaUtils.savePoint(addCapturePoint(player.getLocation(), radius, pointsAwarding, timeToCapture), this.getUID());
                player.sendMessage(Messages.messageWithPrefix("game-koth-point-created-message", ("&aSuccessfully created point! With id : %id% and radius : %radius% and points : %points% and time to capture : %time%")
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
            player.sendMessage(Messages.messageWithPrefix("game-koth-team-amount-error-message", "&c&lTeam id must be between 1 and " + teamsAmount + "!"));
            return true;
        }
        KOTHArenaUtils.saveSpawn(addSpawnLocation(player.getLocation(), team_id), this.getUID());
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
        KOTHSpawn temp = new KOTHSpawn(location, team_id, this);
        spawns.add(temp);
        return spawns.get(spawns.size() - 1);
    }

    public void removeSpawnLocation(String id) {
        spawns.removeIf(spawn -> spawn.getID() == id);
        KOTHArenaUtils.delSpawn(id);
    }

    private void removePoint(String id) {
        points.removeIf(point -> point.getID() == id);
        KOTHArenaUtils.delPoint(id);
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

    @Override
    public void savePlaySpace() {
        KOTHArenaUtils.saveArena(this);
    }

    public void addSpawn(KOTHSpawn spawn) {
        spawns.add(spawn);
    }

    public int getTeamsAmount() {
        return teamsAmount;
    }

    public void setTeamsAmount(int readObject) {
        this.teamsAmount = readObject;
        KOTHArenaUtils.updateArenas(this.getUID(), "koth", "teams_amount", this.teamsAmount);
    }


    public List<KOTHTeam> getTeams() {
        return teams;
    }


    public void setMatchTime(int readObject) {
        this.matchTime = readObject;
        this.overtimeMAX = matchTime / 3;
    }

    public int getMatchTime() {
        return matchTime;
    }

    public void setInitialPointsActive(int readObject) {
        this.initial_points_active = readObject;
    }

    public int getInitialPointsActive() {
        return initial_points_active;
    }

}

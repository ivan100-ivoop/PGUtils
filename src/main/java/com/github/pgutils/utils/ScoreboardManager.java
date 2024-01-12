package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class ScoreboardManager {
    private final Map<Player, Scoreboard> playerScoreboards;
    private final Map<Integer, Scoreboard> teamsScoreboards;
    private final Map<Integer, String> teamsColor;
    private final Map<Integer, Integer> teamsPoint;
    private final ConfigurationSection sbConfig;
    private int time = 0;

    public ScoreboardManager() {
        this.sbConfig = PGUtils.getPlugin(PGUtils.class).getConfig().getConfigurationSection("game-sb");
        this.playerScoreboards = new HashMap<>();
        this.teamsScoreboards = new HashMap<>();
        this.teamsColor = new HashMap<>();
        this.teamsPoint = new HashMap<>();
    }

    public void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        playerScoreboards.remove(player);
    }

    public void addTeam(Integer team, String color, Integer gameID) {
        this.teamsColor.put(team, color);
        for (Player p : playerScoreboards.keySet()) {
            updateGameScore(p, gameID);
        }
    }

    public void addTeamPoint(Integer team, Integer gameID, Integer points) {
        teamsPoint.merge(team, points, Integer::sum);
        for (Player p : playerScoreboards.keySet()) {
            updateGameScore(p, gameID);
        }
    }

    public void setTeamPoint(Integer team, Integer gameID, Integer points) {
        teamsPoint.put(team, points);
        for (Player p : playerScoreboards.keySet()) {
            updateGameScore(p, gameID);
        }
    }

    public void setTime(Integer time, int gameId) {
        this.time = time;
        for (Player p : playerScoreboards.keySet()) {
            updateGameScore(p, gameId);
        }
    }

    private int getPoint(int teamID) {
        if (!teamsPoint.containsKey(teamID)) {
            teamsPoint.put(teamID, 0);
        }
        return teamsPoint.get(teamID);
    }

    public void removeGameScore(int gameID) {
        teamsPoint.clear();
        teamsColor.clear();

        for (Player p : playerScoreboards.keySet()) {
            Scoreboard scoreboard = playerScoreboards.get(p);
            Objective objective = scoreboard.getObjective("kothScore" + gameID);
            if (objective != null) {
                objective.unregister();
            }
            removeScoreboard(p);
        }
    }

    public void createGameScoreboard(Player player, int lobbyID) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("kothScore" + lobbyID, "dummy", GeneralUtils.fixColors(sbConfig.getString("title", "TestScore")));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int index = 0;
        if (!sbConfig.isString("lines")) {
            List<String> lines = sbConfig.getStringList("lines");
            index = lines.size();
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.contains("%teams%")) {
                    for (Map.Entry<Integer, String> teams : teamsColor.entrySet()) {
                        objective.getScore(GeneralUtils.fixColors(getTeamString(teams.getKey(), teams.getValue(), getPoint(teams.getKey())))).setScore(index);
                        index--;
                    }
                } else {
                    if (line.contains("%time%")) {
                        line = line.replace("%time%", String.valueOf(time));
                    }

                    objective.getScore(GeneralUtils.fixColors(line)).setScore(index);
                    index--;
                }
            }
        } else {
            objective.getScore(GeneralUtils.fixColors(fixPlaceHolders(sbConfig.getString("lines")))).setScore(index);
        }

        player.setScoreboard(scoreboard);
        teamsScoreboards.put(lobbyID, scoreboard);
        playerScoreboards.put(player, scoreboard);
    }

    public void updateGameScore(Player player, int id) {
        Scoreboard scoreboard = playerScoreboards.get(player);
        if (scoreboard != null) {
            Objective objective = scoreboard.getObjective("kothScore" + id);
            if (objective != null) {
                for (String entry : scoreboard.getEntries()) {
                    scoreboard.resetScores(entry);
                }
                int index = 0;
                if (!sbConfig.isString("lines")) {
                    List<String> lines = sbConfig.getStringList("lines");
                    index = lines.size();
                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);

                        if (line.contains("%teams%")) {
                            for (Map.Entry<Integer, String> teams : teamsColor.entrySet()) {
                                objective.getScore(GeneralUtils.fixColors(getTeamString(teams.getKey(), teams.getValue(), getPoint(teams.getKey())))).setScore(index);
                                index--;
                            }
                        } else {
                            if (line.contains("%time%")) {
                                line = line.replace("%time%", String.valueOf(time));
                            }

                            objective.getScore(GeneralUtils.fixColors(line)).setScore(index);
                            index--;
                        }
                    }
                } else {
                    objective.getScore(GeneralUtils.fixColors(fixPlaceHolders(sbConfig.getString("lines")))).setScore(index);
                }
            }
        }
    }

    private String fixPlaceHolders(String line) {

        if (line.contains("%time%")) {
            line = line.replace("%time%", String.valueOf(time));
        }

        if (line.contains("%teams%")) {
            StringBuilder teams = new StringBuilder();
            teamsColor.forEach((key, value) -> {
                teams.append(getTeamString(key, value, getPoint(key)));
            });
            line = line.replace("%teams%", teams.toString());
        }

        return line;
    }

    private String getTeamString(int teamID, String color, int points) {
        return sbConfig.getString("teams", "%team_color%Team %team_id%: %team_point%")
                .replace("%team_color%", color)
                .replace("%team_id%", String.valueOf(teamID))
                .replace("%team_point%", String.valueOf(points));
    }

    public Scoreboard getScoreboard(int lobbyID) {
        if (teamsScoreboards.containsKey(lobbyID)) {
            return teamsScoreboards.get(lobbyID);
        }
        return Bukkit.getScoreboardManager().getNewScoreboard();
    }


}
package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {
    private final Map<Player, Scoreboard> playerScoreboards;
    private final Map<Integer, String> teamsColor;
    private final Map<Integer, Integer> teamsPoint;
    private final ConfigurationSection sbConfig;
    private int time = 0;

    public ScoreboardManager() {
        this.sbConfig = PGUtils.getPlugin(PGUtils.class).getConfig().getConfigurationSection("game-sb");
        this.playerScoreboards = new HashMap<>();
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
    public void addTeamPoint(Integer team, Integer gameID) {
        teamsPoint.merge(team, 1, Integer::sum);
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
    private int getPoint(int teamID){
        if(!teamsPoint.containsKey(teamID)){
            teamsPoint.put(teamID, 0);
        }
        return teamsPoint.get(teamID);
    }
    public void clearAll(int gameID) {
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
    public void createGameScoreboard(Player player, int gameID) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("kothScore" + gameID, "dummy", GeneralUtils.fixColors(sbConfig.getString("title", "TestScore")));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int index = 0;
        for (Map.Entry<Integer, String> entry : teamsColor.entrySet()) {
            objective.getScore(getTeamString(entry.getKey(), entry.getValue(), getPoint(entry.getKey()))).setScore(index);
            index++;
        };
        objective.getScore(getTimerString()).setScore(index);

        player.setScoreboard(scoreboard);
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
                for (Map.Entry<Integer, String> entry : teamsColor.entrySet()) {
                    objective.getScore(getTeamString(entry.getKey(), entry.getValue(), getPoint(entry.getKey()))).setScore(index);
                    index++;
                };
                objective.getScore(getTimerString()).setScore(index);


            }
        }
    }
    private String getTimerString() {
        return GeneralUtils.fixColors(sbConfig.getString("timer", "Time: %time%").replace("%time%", String.valueOf(time)));
    }
    private String getTeamString(int teamID, String color, int points){
        return GeneralUtils.fixColors(sbConfig.getString("teams", "%team_color%Team %team_id%: %team_point%")
                .replace("%team_color%", color)
                .replace("%team_id%", String.valueOf(teamID))
                .replace("%team_point%", String.valueOf(points)));
    }
}

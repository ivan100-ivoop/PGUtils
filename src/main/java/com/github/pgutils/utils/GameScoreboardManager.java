package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import com.github.pgutils.utils.sb.Team;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameScoreboardManager {
    private final ConfigurationSection sbConfig;
    private final Map<Integer, List<FastBoard>> playerScoreboards;
    private final Map<Integer, List<Team>> teams;
    private final Map<Integer, Scoreboard> gameScor;

    public GameScoreboardManager() {
        this.sbConfig = PGUtils.getPlugin(PGUtils.class).getConfig().getConfigurationSection("game-sb");
        this.playerScoreboards = new HashMap<>();
        this.teams = new HashMap<>();
        this.gameScor = new HashMap<>();
    }

    public void addTeam(Integer teamID, String color, Integer gameID) {
        if (!teams.containsKey(gameID))
            teams.put(gameID, new ArrayList<>());

        teams.get(gameID).add(new Team()
                .setId(teamID)
                .setColor(color)
                .setPoints(0)
        );
        this.update(gameID);
    }

    public void addTeamPoint(Integer teamID, Integer gameID) {
        List<Team> game = teams.get(gameID);
        if (game != null) {
            for (Team team1 : game) {
                if (team1.getId() == teamID) {
                    team1.addPoint();
                }
            }
            this.update(gameID);
        }
    }

    public void setTeamPoint(Integer teamID, Integer points, Integer gameID) {
        List<Team> game = teams.get(gameID);
        if (game != null) {
            for (Team team1 : game) {
                if (team1.getId() == teamID) {
                    team1.setPoints(points);
                }
            }
            this.update(gameID);
        }
    }

    public void setTime(Integer time, int gameId) {
        List<Team> game = teams.get(gameId);
        if (game != null) {
            for (Team team1 : game) {
                team1.setTime(time);
            }
            this.update(gameId);
        }
    }

    private int getPoint(int teamId, Integer gameID) {
        List<Team> game = teams.get(gameID);
        if (game != null) {
            for (Team team1 : game) {
                if (team1.getId() == teamId) {
                    return team1.getPoints();
                }
            }
        }
        return 0;
    }

    private int getTime(int gameId) {
        List<Team> game = teams.get(gameId);
        if (game != null) {
            for (Team team1 : game) {
                return team1.getTime();
            }
        }
        return 0;
    }

    public void removeGameScore(int gameID) {
        for (FastBoard _sb : playerScoreboards.get(gameID)) {
            if (!_sb.isDeleted()) {
                _sb.delete();
            }
        }
        teams.remove(gameID);
    }

    public void createGameScoreboard(Player player, int gameID) {
        if (!playerScoreboards.containsKey(gameID))
            playerScoreboards.put(gameID, new ArrayList<>());

        FastBoard board = new FastBoard(player);
        playerScoreboards.get(gameID).add(board);

        board.updateTitle(Component.text(GeneralUtils.fixColors(sbConfig.getString("title", "TestScore"))));
        this.update(gameID);
    }

    private void update(int gameID) {
        if (playerScoreboards.containsKey(gameID)) {
            if (!sbConfig.isString("lines")) {
                this.updateLinesScore(gameID);
            } else {
                this.updateStringScore(gameID);
            }
        }
    }

    private void updateLinesScore(int gameID) {
        List<Component> lines = new ArrayList<>();
        int time = this.getTime(gameID);

        if (playerScoreboards.containsKey(gameID)) {
            for (FastBoard sb : playerScoreboards.get(gameID)) {
                for (String line : sbConfig.getStringList("lines")) {
                    if (line.contains("%teams%")) {
                        for (Team team : teams.get(gameID)) {
                            lines.add(Component.text(GeneralUtils.fixColors(line.replace("%teams%", ""))).append(this.getTeamString(team.getId(), team.getColor(), team.getPoints())));
                        }
                    } else {
                        if (line.contains("%time%")) {
                            line = line.replace("%time%", GeneralUtils.formatSeconds(time));
                        }
                        lines.add(Component.text(GeneralUtils.fixColors(line)));
                    }
                }
                sb.updateLines(lines);
            }
        }
    }

    private void updateStringScore(int gameID) {
        if (playerScoreboards.containsKey(gameID)) {
            for (FastBoard sb : playerScoreboards.get(gameID)) {
                sb.updateLines(Component.text(GeneralUtils.fixColors(fixPlaceHolders(sbConfig.getString("lines"), gameID))));
            }
        }
    }

    private String fixPlaceHolders(String line, int gameID) {
        int time = this.getTime(gameID);

        if (line.contains("%time%")) {
            line = line.replace("%time%", GeneralUtils.formatSeconds(time));
        }

        if (line.contains("%teams%")) {
            StringBuilder _teams = new StringBuilder();
            for (Team team : teams.get(gameID)) {
                _teams.append(GeneralUtils.fixColors(line.replace("%teams%", "")) + getTeamString(team.getId(), team.getColor(), team.getPoints()));
            }
            line = line.replace("%teams%", _teams.toString());
        }

        return line;
    }

    private Component getTeamString(int teamID, String color, int points) {
        String coloredText = GeneralUtils.fixColors(sbConfig.getString("teams", "Team %team_id%&7: &f%team_point%")
                .replace("%team_id%", String.valueOf(teamID))
                .replace("%team_point%", String.valueOf(points)));
        return Component.text(coloredText).color(TextColor.fromHexString(color));

    }

    public Scoreboard getScoreboard(int gameID) {
        if (!this.gameScor.containsKey(gameID))
            this.gameScor.put(gameID, Bukkit.getScoreboardManager().getNewScoreboard());
        return this.gameScor.get(gameID);
    }

    public void removeScoreboard(Player player) {

    }
}
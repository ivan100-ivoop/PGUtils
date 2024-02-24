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

import java.util.*;

public class GameScoreboardManager {
    private final ConfigurationSection sbConfig;
    private final Map<Integer, List<Team>> teams;
    private final Map<Integer, Scoreboard> gameScor;

    public GameScoreboardManager() {
        this.sbConfig = PGUtils.getPlugin(PGUtils.class).getConfig().getConfigurationSection("game-sb");
        this.teams = new HashMap<>();
        this.gameScor = new HashMap<>();
    }

    public void addTeam(Integer gameID, Integer teamID, String color) {
        teams.computeIfAbsent(gameID, k -> new ArrayList<>());

        if (!this.containsTeam(gameID, teamID)) {
            teams.get(gameID).add(new Team()
                    .setId(teamID)
                    .setColor(color)
                    .setPoints(0)
            );
        } else {
            this.getTeam(gameID, teamID)
                    .setColor(color);
        }
        this.update(gameID);
    }

    public void addPlayer(int gameID, int teamID, Player player) {
        List<Team> game = teams.get(gameID);
        if (game != null) {
            for (Team team1 : game) {
                if (team1.getId() == teamID) {
                    team1.addPlayer(player);
                    FastBoard board = new FastBoard(player);
                    team1.addScoreboard(board);
                }
            }
            this.update(gameID);
        }
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

    private Team getTeam(int gameID, int teamID) {
        List<Team> gameTeams = teams.get(gameID);
        if (gameTeams != null) {
            for (Team team : gameTeams) {
                if (team.getId() == teamID) {
                    return team;
                }
            }
        }
        return null;
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

    public boolean hasGame(int gameID) {
        return teams.containsKey(gameID);
    }

    private boolean containsTeam(int gameID, int teamID) {
        List<Team> gameTeams = teams.get(gameID);
        if (gameTeams != null) {
            for (Team team : gameTeams) {
                if (team.getId() == teamID) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removePoint(Integer teamID, Integer gameID) {
        List<Team> game = teams.get(gameID);
        if (game != null) {
            for (Team team1 : game) {
                if (team1.getId() == teamID) {
                    team1.removePoint();
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

    public void removeGameScore(int gameID) {
        for (Team team : teams.get(gameID)) {
            for (FastBoard _sb : team.getScoreboards()) {
                if (_sb != null && !_sb.isDeleted()) {
                    _sb.delete();
                }
                team.setPoints(0);
            }
        }
        teams.remove(gameID);
    }

    public void removeScoreboard(Player player) {
        teams.forEach((gameID, gameTeams) -> {
            gameTeams.forEach(team -> {
                Iterator<Player> playerIterator = team.getPlayers().iterator();
                while (playerIterator.hasNext()) {
                    Player pl = playerIterator.next();
                    if (pl == player) {
                        Iterator<FastBoard> scoreboardIterator = team.getScoreboards().iterator();
                        while (scoreboardIterator.hasNext()) {
                            FastBoard scoreboard = scoreboardIterator.next();
                            if (scoreboard != null && !scoreboard.isDeleted() && scoreboard.getPlayer() == player) {
                                scoreboard.delete();
                                scoreboardIterator.remove();
                            }
                        }
                        playerIterator.remove();
                    }
                }
            });
        });
    }

    public void createGameScoreboard(int gameID) {
        if (teams.containsKey(gameID)) {
            for (Team team : teams.get(gameID)) {
                for (FastBoard board : team.getScoreboards()) {
                    board.updateTitle(Component.text(GeneralUtils.fixColors(sbConfig.getString("title", "TestScore"))));
                }
            }
        }
        this.update(gameID);
    }

    private void update(int gameID) {
        if (teams.containsKey(gameID)) {
            if (!sbConfig.isString("lines")) {
                this.updateLinesScore(gameID);
            } else {
                this.updateStringScore(gameID);
            }
        }
    }

    private void updateLinesScore(int gameID) {
        if (teams.containsKey(gameID)) {
            for (Team team : teams.get(gameID)) {
                for (FastBoard sb : team.getScoreboards()) {
                    if (sb != null && !sb.isDeleted()) {
                        List<Component> lines = new ArrayList<>();
                        for (String line : sbConfig.getStringList("lines")) {
                            if (line.contains("%teams%")) {
                                this.getComponentLines(gameID, lines, Component.text(GeneralUtils.fixColors(line.replace("%teams%", ""))));
                            } else {
                                if (line.contains("%time%")) {
                                    line = line.replace("%time%", GeneralUtils.formatSeconds(team.getTime()));
                                }
                                lines.add(Component.text(GeneralUtils.fixColors(line)));
                            }
                        }
                        sb.updateLines(lines);
                    }
                }
            }
        }
    }

    private void getComponentLines(int gameID, List<Component> lines, Component defaultComponent) {
        for (Team team : teams.get(gameID)) {
            lines.add(defaultComponent.append(this.getTeamString(team.getId(), team.getColor(), team.getPoints())));
        }
    }

    private void updateStringScore(int gameID) {
        for (Team team : teams.get(gameID)) {
            for (FastBoard sb : team.getScoreboards()) {
                if (sb != null && !sb.isDeleted())
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

}
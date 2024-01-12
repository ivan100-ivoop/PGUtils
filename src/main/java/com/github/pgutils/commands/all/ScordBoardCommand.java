package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.utils.PGSubCommand;
import com.github.pgutils.utils.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.swing.text.PlainDocument;
import java.util.Collections;
import java.util.List;

public class ScordBoardCommand extends PGSubCommand {
    private ScoreboardManager sb;
    private int time = 0;

    public  ScordBoardCommand(){
        sb = new ScoreboardManager();
    }

    @Override
    public String getName() {
        return "sb";
    }

    @Override
    public String getDescription() {
        return "Testing";
    }

    @Override
    public String getPermission() {
        return "pgutil.sb";
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            sb.createGameScoreboard(player, 1);
            sb.addTeam(1, "&a", 1);
            sb.addTeam(2, "&b", 1);
            Bukkit.getScheduler().runTaskTimer(PGUtils.getPlugin(PGUtils.class), new Runnable() {
                @Override
                public void run() {
                    time++;
                    sb.setTime(time, 1);
                    sb.addTeamPoint(1, 1);
                    sb.addTeamPoint(2, 1);
                    if(time > 40){
                        time = 0;
                        sb.removeGameScore(1);
                    }
                }
            }, 0, 20);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}

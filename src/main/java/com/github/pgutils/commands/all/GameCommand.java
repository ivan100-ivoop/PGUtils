package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.games.KOTHArena;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.entities.games.kothadditionals.KOTHPoint;
import com.github.pgutils.entities.games.kothadditionals.KOTHSpawn;
import com.github.pgutils.utils.PGSubCommand;
import com.github.pgutils.selections.PlayerPlaySpaceSelector;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class GameCommand extends PGSubCommand {
    @Override
    public String getName() {
        return "game";
    }

    @Override
    public String getDescription() {
        return "Game Settings!";
    }

    @Override
    public String getPermission() {
        return "pgutils.game";
    }

    @Override
    public String getUsage() {
        return "/pg game koth create [<args>]";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return false;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args[0].equalsIgnoreCase("koth")) {
                if (args.length >= 2) {

                    if (args[1].equalsIgnoreCase("create")) {
                        if (args.length >= 3) {
                            if (PGUtils.selectedPlaySpace.stream().filter(player_ -> player_.player.equals(player)).findFirst().orElse(null) == null) {
                                player.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cPlayspace is not selected!"));
                                return false;
                            }
                            if (args[2].equalsIgnoreCase("spawn")) {
                                if (args.length >= 4) {
                                    int team = Integer.parseInt(args[3]);

                                    Optional<PlayerPlaySpaceSelector> arena = PGUtils.selectedPlaySpace.stream()
                                            .filter(selector -> selector.player.equals(player))
                                            .findFirst();
                                    if (!arena.isPresent()) {
                                        player.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cPlayspace is not selected!"));
                                    } else {
                                        if (arena.get().playSpace instanceof KOTHArena) {
                                            KOTHArena kothArena = (KOTHArena) arena.get().playSpace;
                                            KOTHSpawn kothSpawn = kothArena.addSpawnLocation(player.getLocation(), team);
                                            player.sendMessage(Messages.messageWithPrefix("create-spawn-message", "&aSuccessful created Spawn Location! With ID: %id% and Team ID: %team%").replace("%id%", kothSpawn.getID() + "").replace("%team%", team + ""));
                                        } else {
                                            player.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cYou need to select a KOTH arena!"));
                                        }
                                    }
                                }
                            }
                            if (args[2].equalsIgnoreCase("point")) {
                                if (args.length == 6) {
                                    int radius = Integer.parseInt(args[3]);
                                    int points = Integer.parseInt(args[4]);
                                    int captureTime = Integer.parseInt(args[5]);
                                    Optional<PlayerPlaySpaceSelector> arena = PGUtils.selectedPlaySpace.stream()
                                            .filter(selector -> selector.player.equals(player))
                                            .findFirst();
                                    if (!arena.isPresent()) {
                                        player.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cPlayspace is not selected!"));
                                    } else {
                                        if (arena.get().playSpace instanceof KOTHArena) {
                                            KOTHArena kothArena = (KOTHArena) arena.get().playSpace;
                                            KOTHPoint kothPoint = kothArena.addCapturePoint(player.getLocation(), radius, points, captureTime);
                                            player.sendMessage(Messages.messageWithPrefix("create-point-message", "&aSuccessful created Point Location! With ID: %id% and radius: %radius% and points: %points% and capture time: %time%")
                                                    .replace("%id%", kothPoint.getID() + "")
                                                    .replace("%radius%", radius + "")
                                                    .replace("%points%", points + "")
                                                    .replace("%time%", captureTime + ""));
                                        } else {
                                            player.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cYou need to select a KOTH arena!"));
                                        }
                                    }
                                }
                                if (args.length == 5) {
                                    int radius = Integer.parseInt(args[3]);
                                    int points = Integer.parseInt(args[4]);
                                    Optional<PlayerPlaySpaceSelector> arena = PGUtils.selectedPlaySpace.stream()
                                            .filter(selector -> selector.player.equals(player))
                                            .findFirst();
                                    if (!arena.isPresent()) {
                                        player.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cPlayspace is not selected!"));
                                    } else {
                                        if (arena.get().playSpace instanceof KOTHArena) {
                                            KOTHArena kothArena = (KOTHArena) arena.get().playSpace;
                                            KOTHPoint kothPoint =  kothArena.addCapturePoint(player.getLocation(), radius, points);
                                            player.sendMessage(Messages.messageWithPrefix("create-point-message", "&aSuccessful created Point Location! With ID: %id% and radius: %radius% and points: %points% and capture time: %time%")
                                                    .replace("%id%", kothPoint.getID() + "")
                                                    .replace("%radius%", radius + "")
                                                    .replace("%points%", points + ""));
                                        } else {
                                            player.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cYou need to select a KOTH arena!"));
                                        }
                                    }
                                }
                                else if (args.length == 4) {
                                    int radius = Integer.parseInt(args[3]);
                                    Optional<PlayerPlaySpaceSelector> arena = PGUtils.selectedPlaySpace.stream()
                                            .filter(selector -> selector.player.equals(player))
                                            .findFirst();
                                    if (!arena.isPresent()) {
                                        player.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cPlayspace is not selected!"));
                                    } else {
                                        if (arena.get().playSpace instanceof KOTHArena) {
                                            KOTHArena kothArena = (KOTHArena) arena.get().playSpace;
                                            KOTHPoint kothPoint =  kothArena.addCapturePoint(player.getLocation(), radius);
                                            player.sendMessage(Messages.messageWithPrefix("create-point-message", "&aSuccessful created Point Location! With ID: %id% and radius: %radius% and points: %points% and capture time: %time%")
                                                    .replace("%id%", kothPoint.getID() + "")
                                                    .replace("%radius%", radius + ""));
                                        } else {
                                            player.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cYou need to select a KOTH arena!"));
                                        }
                                    }
                                }
                                Optional<PlayerPlaySpaceSelector> arena = PGUtils.selectedPlaySpace.stream()
                                        .filter(selector -> selector.player.equals(player))
                                        .findFirst();
                                if (!arena.isPresent()) {
                                    player.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cPlayspace is not selected!"));
                                } else {
                                    if (arena.get().playSpace instanceof KOTHArena) {
                                        KOTHArena kothArena = (KOTHArena) arena.get().playSpace;
                                        kothArena.addCapturePoint(player.getLocation());
                                        KOTHPoint kothPoint =  kothArena.addCapturePoint(player.getLocation());
                                        player.sendMessage(Messages.messageWithPrefix("create-point-message", "&aSuccessful created Point Location! With ID: %id% and radius: %radius% and points: %points% and capture time: %time%")
                                                .replace("%id%", kothPoint.getID() + ""));
                                    } else {
                                        player.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cYou need to select a KOTH arena!"));
                                    }
                                }

                            }
                        }
                        else {
                            KOTHArena arena = new KOTHArena();
                            arena.setPos(player.getLocation());
                            player.sendMessage(Messages.messageWithPrefix("create-arena-message", "&aSuccessful created KOTH Arena!"));
                            GeneralUtils.playerSelectPlaySpace(player, arena);
                        }

                    }
                    if (args[1].equalsIgnoreCase("set")) {
                        Optional<PlayerPlaySpaceSelector> arena = PGUtils.selectedPlaySpace.stream()
                                .filter(selector -> selector.player.equals(player))
                                .findFirst();
                        if (!arena.isPresent()) {
                            player.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cPlayspace is not selected!"));
                        } else {
                            if (arena.get().playSpace instanceof KOTHArena) {
                                KOTHArena kothArena = (KOTHArena) arena.get().playSpace;
                                kothArena.setPos(player.getLocation());
                                player.sendMessage(Messages.messageWithPrefix("set-arena-message", "&aSuccessful set KOTH Arena Location!"));
                            } else {
                                player.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cYou need to select a KOTH arena!"));
                            }
                        }
                    }
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("select")) {
                if (args.length >= 2) {
                    int id = Integer.parseInt(args[1]);
                    PlaySpace playSpace = PlaySpace.playSpaces.stream()
                            .filter(space -> space.getID() == id)
                            .findFirst()
                            .orElse(null);
                    if (playSpace == null) {
                        player.sendMessage(Messages.messageWithPrefix("missing-arena-playspace-message", "&cPlaySpace is not found!"));
                    } else {
                        GeneralUtils.playerSelectPlaySpace(player, playSpace);
                        player.sendMessage(Messages.messageWithPrefix("select-arena-playspace-message", "&aSuccessful selected %gamename%").replace("%gamename%", playSpace.getType()));
                    }
                }
                return true;
            }

            // TODO: FIX THIS
            if (args[0].equalsIgnoreCase("delete")) {
                if (args.length >= 2) {
                    int id = Integer.parseInt(args[1]);
                    PlaySpace playSpace = PlaySpace.playSpaces.stream()
                            .filter(space -> space.getID() == id)
                            .findFirst()
                            .orElse(null);
                    if (playSpace == null) {
                        player.sendMessage(Messages.messageWithPrefix("missing-arena-playspace-message", "&cPlaySpace is not found!"));
                    } else {
                        Lobby lobby = Lobby.lobbies.stream()
                                .filter(lobby_ -> lobby_.getPlaySpaces().contains(playSpace))
                                .findFirst()
                                .orElse(null);
                        if (lobby != null) {
                            if (lobby.getCurrentPlaySpace() == playSpace) {
                                lobby.getCurrentPlaySpace().end();
                                lobby.setCurrentPlaySpace(null);
                            }
                            lobby.removePlaySpace(playSpace);
                        }
                        PlaySpace.playSpaces.remove(playSpace);
                        PGUtils.selectedPlaySpace.remove(PGUtils.selectedPlaySpace.stream()
                                .filter(selector -> selector.playSpace.equals(playSpace))
                                .findFirst()
                                .orElse(null));
                        player.sendMessage(Messages.messageWithPrefix("delete-arena-playspace-message", "&aSuccessful deleted"));
                    }
                }

            }
            if (args[0].equalsIgnoreCase("set")) {
                Optional<PlayerPlaySpaceSelector> arena = PGUtils.selectedPlaySpace.stream()
                        .filter(selector -> selector.player.equals(player))
                        .findFirst();
                if (!arena.isPresent()) {
                    player.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cPlayspace is not selected!"));
                } else {
                    if (arena.get().playSpace instanceof KOTHArena) {
                        KOTHArena kothArena = (KOTHArena) arena.get().playSpace;
                        kothArena.setPos(player.getLocation());
                        player.sendMessage(Messages.messageWithPrefix("set-arena-message", "&aSuccessful set KOTH Arena Location!"));
                    } else {
                        player.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cYou need to select a KOTH arena!"));
                    }
                }
            }


        }
        return false;
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("koth", "set", "select", "delete");
        }

        if (args.length == 2 && args[0].equals("koth")){
            return Arrays.asList("create", "set");
        }

        if (args.length >= 3 && args[1].equals("create")){
            return Arrays.asList("spawn", "point");
        }

        if (args.length == 2 && (args[0].equals("delete") || args[0].equals("select"))){
            List<String> all = new ArrayList<>();
            for(Lobby lobby : Lobby.lobbies){
                all.add("" + lobby.getID());
            }
            return all;
        }

        return Collections.emptyList();
    }
}

package com.github.pgutils.commands.all;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.KOTHArena;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.interfaces.PGSubCommand;
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
        if(args.length < 1){
            sender.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return false;
        }

        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (args[0].equalsIgnoreCase("koth")) {
                if (args[1].equalsIgnoreCase("create")) {
                    if(args.length >= 2) {
                        if (args[2].equalsIgnoreCase("arena")) {
                            KOTHArena arena = new KOTHArena();
                            arena.setPos(player.getLocation());
                            sender.sendMessage(Messages.messageWithPrefix("create-arena-message", "&aSuccessful created Arena! N%arena%").replace("%arena%", "" + arena.getID()));
                            GeneralUtils.playerSelectPlaySpace(player, arena);
                            return true;
                        }

                        if (args[2].equalsIgnoreCase("spawn")) {
                            Optional<PlayerPlaySpaceSelector> arena = PGUtils.selectedPlaySpace.stream()
                                    .filter(selector -> selector.player.equals(player))
                                    .findFirst();
                            if (!arena.isPresent()) {
                                player.sendMessage(Messages.messageWithPrefix("missing-arena-playspace-message", "&cPlayspace is not selected!"));
                            } else {
                                if (arena.get().playSpace instanceof KOTHArena) {
                                    KOTHArena kothArena = (KOTHArena) arena.get().playSpace;
                                    kothArena.addSpawnLocation(player.getLocation());
                                    sender.sendMessage(Messages.messageWithPrefix("create-spawn-message", "&aSuccessful created Spawn Location!"));
                                    return true;
                                } else {
                                    sender.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cYou need to select a KOTH arena!"));
                                }
                            }
                            return false;
                        }

                        if (args[2].equalsIgnoreCase("point")) {
                            if (args.length == 2) {
                                int radius = Integer.parseInt(args[3]);
                                int points = Integer.parseInt(args[4]);
                                Optional<PlayerPlaySpaceSelector> arena = PGUtils.selectedPlaySpace.stream()
                                        .filter(selector -> selector.player.equals(player))
                                        .findFirst();
                                if (!arena.isPresent()) {
                                    player.sendMessage(Messages.messageWithPrefix("missing-arena-playspace-message", "&cPlayspace is not selected!"));
                                } else {
                                    if (arena.get().playSpace instanceof KOTHArena) {
                                        KOTHArena kothArena = (KOTHArena) arena.get().playSpace;
                                        kothArena.addCapturePoint(player.getLocation(), radius, points);
                                        sender.sendMessage(Messages.messageWithPrefix("create-point-message", "&aSuccessful created Point Location!"));
                                        return true;
                                    } else {
                                        sender.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cYou need to select a KOTH arena!"));
                                    }
                                }
                            }

                            if (args.length == 3) {
                                int radius = Integer.parseInt(args[3]);
                                Optional<PlayerPlaySpaceSelector> arena = PGUtils.selectedPlaySpace.stream()
                                        .filter(selector -> selector.player.equals(player))
                                        .findFirst();
                                if (!arena.isPresent()) {
                                    player.sendMessage(Messages.messageWithPrefix("missing-arena-playspace-message", "&cPlayspace is not selected!"));
                                } else {
                                    if (arena.get().playSpace instanceof KOTHArena) {
                                        KOTHArena kothArena = (KOTHArena) arena.get().playSpace;
                                        kothArena.addCapturePoint(player.getLocation(), radius);
                                        sender.sendMessage(Messages.messageWithPrefix("create-point-message", "&aSuccessful created Point Location!"));
                                        return true;
                                    } else {
                                        sender.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cYou need to select a KOTH arena!"));
                                    }
                                }
                            }

                            Optional<PlayerPlaySpaceSelector> arena = PGUtils.selectedPlaySpace.stream()
                                    .filter(selector -> selector.player.equals(player))
                                    .findFirst();
                            if (!arena.isPresent()) {
                                player.sendMessage(Messages.messageWithPrefix("missing-arena-playspace-message", "&cPlayspace is not selected!"));
                            } else {
                                if (arena.get().playSpace instanceof KOTHArena) {
                                    KOTHArena kothArena = (KOTHArena) arena.get().playSpace;
                                    kothArena.addCapturePoint(player.getLocation());
                                    sender.sendMessage(Messages.messageWithPrefix("create-point-message", "&aSuccessful created Point Location!"));
                                    return true;
                                } else {
                                    sender.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cYou need to select a KOTH arena!"));
                                }
                            }
                            return false;
                        }
                    }
                    return false;
                }

                if (args[1].equalsIgnoreCase("set")) {
                        Optional<PlayerPlaySpaceSelector> arena = PGUtils.selectedPlaySpace.stream()
                                .filter(selector -> selector.player.equals(player))
                                .findFirst();
                        if (!arena.isPresent()) {
                            player.sendMessage(Messages.messageWithPrefix("missing-arena-playspace-message", "&cPlayspace is not selected!"));
                        } else {
                            if (arena.get().playSpace instanceof KOTHArena) {
                                KOTHArena kothArena = (KOTHArena) arena.get().playSpace;
                                kothArena.setPos(player.getLocation());
                                sender.sendMessage(Messages.messageWithPrefix("set-arena-message", "&aSuccessful set Arena Location!"));
                                return true;
                            } else {
                                sender.sendMessage(Messages.messageWithPrefix("missing-arena-message", "&cYou need to select a KOTH arena!"));
                            }
                        }
                    }
                     return false;
                }

                if (args[0].equalsIgnoreCase("select")) {
                    if (args.length >= 1) {
                        int id = Integer.parseInt(args[1]);
                        PlaySpace playSpace = PlaySpace.playSpaces.stream()
                                .filter(space -> space.getID() == id)
                                .findFirst()
                                .orElse(null);
                        if (playSpace == null) {
                            sender.sendMessage(Messages.messageWithPrefix("missing-arena-playspace-message", "&cPlaySpace is not found!"));
                        } else {
                            GeneralUtils.playerSelectPlaySpace(player, playSpace);
                            sender.sendMessage(Messages.messageWithPrefix("select-arena-message", "&aSuccessful selected %arena%&a!").replace("%arena%", playSpace.getType()));
                            return true;
                        }
                    }
                    return false;
                }

                if (args[0].equalsIgnoreCase("delete")) {
                    if (args.length >= 1) {
                        int id = Integer.parseInt(args[1]);
                        PlaySpace playSpace = PlaySpace.playSpaces.stream()
                                .filter(space -> space.getID() == id)
                                .findFirst()
                                .orElse(null);
                        if (playSpace == null) {
                            sender.sendMessage(Messages.messageWithPrefix("missing-arena-playspace-message", "&cPlaySpace is not found!"));
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
                            player.sendMessage(Messages.messageWithPrefix("delete-arena-message", "&aSuccessful deleted %arena%&a!").replace("%arena%" , playSpace.getType()));
                            return true;
                        }
                    }
                    return false;
                }

            }
            sender.sendMessage(Messages.messageWithPrefix("invalid-command-message", "&4Invalid command."));
            return false;
        }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("koth", "set", "select", "delete");
        }

        if (args.length == 2 && args[0].equals("koth")){
            return Collections.singletonList("create");
        }

        if (args.length >= 3 && args[1].equals("create")){
            return Arrays.asList("arena", "spawn", "point");
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

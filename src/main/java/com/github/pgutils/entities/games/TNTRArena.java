package com.github.pgutils.entities.games;

import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.customitems.effects.HumanBombEffect;
import com.github.pgutils.customitems.effects.LostBombEffect;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.entities.games.tntradditionals.TNTRJump;
import com.github.pgutils.entities.games.tntradditionals.TNTRPower;
import com.github.pgutils.entities.games.tntradditionals.TNTRSpawn;
import com.github.pgutils.enums.GameStatus;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PlayerManager;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class TNTRArena extends PlaySpace {

    List<TNTRSpawn> spawns;

    List<TNTRJump> jumps;

    List<TNTRPower> powers;

    private int startingTick = 0;

    // Saved
    private int bombRatio = 3;

    private int nameOfGameTime = 30;

    private int infoTime = 30;

    private int startingTime = nameOfGameTime  + infoTime + 60;

    // Saved
    private int bombTimer = 3000;

    public static List<String> addGameObjects = Arrays.asList("spawn", "jump-pad", "power-up");

    public static List<String> setGameObjects = Arrays.asList("select-closest", "delete-closest");

    private int endingTick;

    private int endingTime = 200;

    public TNTRArena() {
        super();
        type = "TNTRun";
        this.spawns = new ArrayList<>();
        this.jumps = new ArrayList<>();
        this.powers = new ArrayList<>();
        getSetMap().put("bomb-timer", this::setBombTimer);
        getSetMap().put("bomb-ratio", this::setBombRatio);
    }



    @Override
    public void start() {
        Collections.shuffle(players);
        Collections.shuffle(spawns);

        for (int i = 0; i < players.size(); i++) {
            PlayerManager.disableMove(players.get(i));
            PlayerManager.disablePVP(players.get(i));
        }
        for (int i = 0; i < players.size(); i++) {
            players.get(i).teleport(spawns.get(i % spawns.size()).getPos());
        }
        jumps.forEach(jump -> jump.setupForPlayers(players));
    }

    @Override
    public void onUpdate() {
        if (status == GameStatus.STARTING) {
            if (startingTick == 0) {
                players.forEach(player -> {
                    player.sendTitle(Messages.getMessage("game-tntr-name", "§eTNT Run", false), "", 0, 20, 0);
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                });
            }
            else if (startingTick == nameOfGameTime ) {
                players.stream().forEach(player -> {
                    player.sendTitle(Messages.getMessage("game-tntr-info", "§eTag People!", false), Messages.getMessage("game-tntr-runner-subtitle", "§eAvoid the bombhead!", false), 0, 20, 0);
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                });
            }
            else if (startingTick % 20 == (nameOfGameTime +  infoTime) % 20 && startingTick >= nameOfGameTime + infoTime && startingTick < startingTime) {
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
                    PlayerManager.enableMove(player);
                    player.removePotionEffect(PotionEffectType.GLOWING);
                });
                players.stream().filter(player -> !CustomEffect.hasEffect(player, HumanBombEffect.class)).forEach(player -> {
                    ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
                    ItemMeta meta = helmet.getItemMeta();
                    meta.setDisplayName("§f§lParty Hat");
                    meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                    LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) meta;
                    leatherArmorMeta.setColor(Color.WHITE);
                    meta.setUnbreakable(true);
                    helmet.setItemMeta(meta);
                    player.getInventory().setHelmet(helmet);

                    ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                    ItemMeta meta2 = chestplate.getItemMeta();
                    meta2.setDisplayName("§f§lParty Vest");
                    meta2.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                    leatherArmorMeta = (LeatherArmorMeta) meta2;
                    leatherArmorMeta.setColor(Color.WHITE);
                    meta2.setUnbreakable(true);
                    chestplate.setItemMeta(meta2);
                    player.getInventory().setChestplate(chestplate);

                    ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
                    ItemMeta meta3 = leggings.getItemMeta();
                    meta3.setDisplayName("§f§lParty Pants");
                    meta3.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                    leatherArmorMeta = (LeatherArmorMeta) meta3;
                    leatherArmorMeta.setColor(Color.WHITE);
                    meta3.setUnbreakable(true);
                    leggings.setItemMeta(meta3);
                    player.getInventory().setLeggings(leggings);

                    ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
                    ItemMeta meta4 = boots.getItemMeta();
                    meta4.setDisplayName("§f§lParty Shoes");
                    meta4.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                    leatherArmorMeta = (LeatherArmorMeta) meta4;
                    leatherArmorMeta.setColor(Color.WHITE);
                    meta4.setUnbreakable(true);
                    boots.setItemMeta(meta4);
                    player.getInventory().setBoots(boots);
                });

                int numBombs = 1 + ((players.size() >= bombRatio * 2) ? (players.size() / bombRatio) - 1 : 0);

                for (int i = 0; i < players.size(); i++) {
                    if (i < numBombs) {
                        players.get(i).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 1, true));
                        new HumanBombEffect(players.get(i), bombTimer);
                    }
                }
            }
            startingTick++;

        } else if (status == GameStatus.IN_PROGRESS) {
            if (!(players.stream().anyMatch(player -> CustomEffect.hasEffect(player, HumanBombEffect.class)))) {
                status = GameStatus.IS_ENDING;
                players.forEach(player -> {
                    player.sendTitle(Messages.getMessage("game-tntr-end", "§eGame End!", false), Messages.getMessage("game-tntr-end-subtext", "§eAll bombs exploded!", false), 0, 20, 0);
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                });
            }
            jumps.forEach(jump -> jump.updateForPlayers());
        } else if (status == GameStatus.IS_ENDING) {
            endingTick++;
            if (endingTick >= endingTime) {
                List<Player> winners = new ArrayList<>(players.stream().filter(player -> CustomEffect.hasEffect(player, LostBombEffect.class)).collect(Collectors.toList()));
                end(winners);
            }
        }

    }

    @Override
    public void endProcedure() {
        startingTick = 0;
        endingTick = 0;
        jumps.forEach(jump -> jump.reset());
    }

    @Override
    public void deletePlaySpace() {
        //TNTRArenaUtils.deleteArena(this.getUID());
    }

    @Override
    protected void saveName() {
        //TNTRArenaUtils.updateArenas(this.getUID(), "koth", "name", this.getName());
    }

    @Override
    protected void savePos() {
        //TNTRArenaUtils.updateLocation(this.getUID(), this.getPos());
    }

    @Override
    public void onRemovePlayer(Player player) {
        player.getInventory().clear();
    }


    @Override
    public String passesChecks() {
        // Check if there is at least 1 spawn
        if (spawns.size() == 0) {
            return Messages.getMessage("game-tntr-no-spawns", "There are no spawns!",false);
        }

        if (getLobby() != null) {
            if (getLobby().getPlayers().size() < 2) {
                return Messages.getMessage("game-tntr-not-enough-players", "Game needs at least 2 players!", false);
            }
        }

        return "All Done";
    }

    @Override
    public void updateViewGame(Player player) {
        spawns.stream().forEach(spawn -> player.spawnParticle(Particle.VILLAGER_HAPPY, spawn.getPos(), 1, 0.3, 1, 0.3, 0.01));
        jumps.stream().forEach(jump -> player.spawnParticle(Particle.CRIT_MAGIC, jump.getPos(), 1, 0.3, 1, 0.3, 0.01));

    }

    @Override
    public boolean addGameObjects(Player player, String[] args) {
        switch (args[2].toLowerCase()) {
            case "spawn":
                return createSpawn(player, args);

            case "power-up":
                return createPowerup(player, args);

            case "jump-pad":
                return createJumpPad(player, args);
        }
        return false;
    }

    private boolean createPowerup(Player player, String[] args) {
        return false;
    }

    public boolean createSpawn(Player player, String[] args) {
        addSpawnLocation(player.getLocation());
        player.sendMessage(Messages.messageWithPrefix("spawn-created-message", "&aSuccessfully created spawn!"));
        return true;
    }

    public boolean createJumpPad(Player player, String[] args) {
        if (args.length == 3) {
            addJumpLocation(player.getLocation(), 3, 3);
            player.sendMessage(Messages.messageWithPrefix("jump-created-message", "&aSuccessfully created jump!"));
            return true;
        }
        else if (args.length == 4) {
            try {
                double radius = Double.parseDouble(args[3]);
                addJumpLocation(player.getLocation(), radius, 3);
                player.sendMessage(Messages.messageWithPrefix("jump-created-message", "&aSuccessfully created jump!"));
                return true;
            }
            catch (NumberFormatException e) {
                player.sendMessage(Messages.messageWithPrefix("jump-created-message", "&c&lOops &cYou must enter a valid number!"));
                return false;
            }
        }
        else if(args.length == 5) {
            try {
                double radius = Double.parseDouble(args[3]);
                double strength = Double.parseDouble(args[4]);
                addJumpLocation(player.getLocation(), radius, strength);
                player.sendMessage(Messages.messageWithPrefix("jump-created-message", "&aSuccessfully created jump!"));
                return true;
            }
            catch (NumberFormatException e) {
                player.sendMessage(Messages.messageWithPrefix("jump-created-message", "&c&lOops &cYou must enter a valid number!"));
                return false;
            }
        }
        else {
            player.sendMessage(Messages.messageWithPrefix("jump-created-message", "&c&lOops &cYou must enter a valid number!"));
            return false;
        }
    }

    public int addSpawnLocation(Location location) {
        spawns.add(new TNTRSpawn(location));
        //TNTRArenaUtils.saveSpawn(spawns.get(spawns.size() - 1), this.getUID());
        return spawns.size() - 1;
    }

    public int addJumpLocation(Location location, double radius, double strength) {
        jumps.add(new TNTRJump(location, radius, strength));
        //TNTRArenaUtils.saveJump(jumps.get(jumps.size() - 1), this.getUID());
        return jumps.size() - 1;
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
        locations.addAll(jumps.stream().map(jump -> jump.getPos()).collect(Collectors.toList()));
        Location closest = GeneralUtils.getClosestLocation(player.getLocation(), locations);
        if (closest == null) {
            player.sendMessage(Messages.messageWithPrefix("game-tntr-no-closest-object-message", "&c&lOops &cThere are no objects!"));
            return true;
        }

        if (spawns.stream().anyMatch(spawn -> spawn.getPos().equals(closest))) {
            removeSpawnLocation(spawns.stream().filter(spawn -> spawn.getPos().equals(closest)).findFirst().get().getID());
            player.sendMessage(Messages.messageWithPrefix("game-tntr-spawn-deleted-message", "&aSuccessfully deleted spawn!"));
            return true;
        }

        if (jumps.stream().anyMatch(jump -> jump.getPos().equals(closest))) {
            removeJumpLocation(jumps.stream().filter(jump -> jump.getPos().equals(closest)).findFirst().get().getID());
            player.sendMessage(Messages.messageWithPrefix("game-tntr-jump-deleted-message", "&aSuccessfully deleted jump!"));
            return true;
        }

        return false;
    }



    private boolean selectClosest(Player player, String[] args) {
        List<Location> locations = new ArrayList<>();
        locations.addAll(spawns.stream().map(spawn -> spawn.getPos()).collect(Collectors.toList()));
        locations.addAll(jumps.stream().map(jump -> jump.getPos()).collect(Collectors.toList()));
        Location closest = GeneralUtils.getClosestLocation(player.getLocation(), locations);
        if (closest == null) {
            player.sendMessage(Messages.messageWithPrefix("game-tntr-no-closest-object-message", "&c&lOops &cThere are no objects!"));
            return true;
        }

        if (spawns.stream().anyMatch(spawn -> spawn.getPos().equals(closest))) {
            player.sendMessage(Messages.messageWithPrefix("game-tntr-spawn-selected-message", "&aSelected spawn!"));
            return true;
        }

        if (jumps.stream().anyMatch(jump -> jump.getPos().equals(closest))) {
            player.sendMessage(Messages.messageWithPrefix("game-tntr-jump-selected-message", "&aSelected jump!"));
            return true;
        }

        return false;
    }

    private void removeSpawnLocation(String id) {
        spawns.removeIf(spawn -> spawn.getID().equals(id));
        //TNTRArenaUtils.delSpawn(id);
    }

    private void removeJumpLocation(String id) {
        jumps.removeIf(jump -> jump.getID().equals(id));
        //TNTRArenaUtils.delJump(id);
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

    private Boolean setBombRatio(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return true;
        }
        try {
            int bombs = Integer.parseInt(args[3]);
            if (bombs < 2) {
                player.sendMessage(Messages.messageWithPrefix("game-tntr-bomb-ratio-message", "&c&lOops &cYou must enter a number greater than 1!"));
                return true;
            }

            //TNTRArenaUtils.updateArenas(this.getUID(), "koth", "match_time", this.bombRatio);
            player.sendMessage(Messages.messageWithPrefix("game-option-set-message", "&aSuccessfully set game option! With option : %option% and value : %value%")
                    .replace("%option%", args[2])
                    .replace("%value%", args[3]));
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return true;
        }
    }

    private Boolean setBombTimer(Player player, String[] strings) {
        if (strings.length < 4) {
            player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return true;
        }
        try {
            int time = Integer.parseInt(strings[3]);
            if (time < 500) {
                player.sendMessage(Messages.messageWithPrefix("game-tntr-bomb-timer-message", "&c&lOops &cYou must enter a number greater than 500!"));
                return true;
            }

            //TNTRArenaUtils.updateArenas(this.getUID(), "koth", "match_time", this.bombTimer);
            player.sendMessage(Messages.messageWithPrefix("game-option-set-message", "&aSuccessfully set game option! With option : %option% and value : %value%")
                    .replace("%option%", strings[2])
                    .replace("%value%", strings[3]));
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(Messages.messageWithPrefix("command-error-message", "&c&lOops &cthere is an error with the command"));
            return true;
        }
    }

    @Override
    public void savePlaySpace() {
        //TNTRArenaUtils.saveArena(this);
    }

    public void setBombTimer(int bombTimer) {
        this.bombTimer = bombTimer;
    }

    public void setBombRatio(int bombRatio) {
        this.bombRatio = bombRatio;
    }

    public void addSpawnLocation(TNTRSpawn spawn) {
        spawns.add(spawn);
    }

    public void addJumpLocation(TNTRJump jump) {
        jumps.add(jump);
    }

    public List<TNTRJump> getJumps() {
        return jumps;
    }

    public List<TNTRSpawn> getSpawns() {
        return spawns;
    }

    public int getBombTimer() {
        return bombTimer;
    }

    public int getBombRatio() {
        return bombRatio;
    }
}

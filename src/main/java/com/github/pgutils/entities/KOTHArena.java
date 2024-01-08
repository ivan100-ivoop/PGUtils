package com.github.pgutils.entities;

import java.util.*;

import com.github.pgutils.utils.GeneralUtils;

import com.github.pgutils.enums.GameStatus;
import com.github.pgutils.interfaces.EvenIndependent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.util.List;
import java.util.stream.Collectors;

public class KOTHArena extends PlaySpace implements EvenIndependent {
    // Convert all ChatColor colors to Color
    private static final List<String> colors = Arrays.asList("000000", "0000AA", "00AA00", "00AAAA", "AA0000", "AA00AA", "FFAA00", "AAAAAA", "555555", "5555FF", "55FF55", "55FFFF", "FF5555", "FF55FF", "FFFF55");
    private static final List<String> colorGarbage = Arrays.asList("§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7","§8", "§9", "§a", "§b", "§c", "§d", "§e");
    List<Location> spawns = new ArrayList<>();

    List<KOTHPoint> points = new ArrayList<>();

    private int startingTime = 60;

    private int startingTick = 0;

    private int testMessageTime = 10;

    private int testMessageTick = 0;

    private List<KOTHTeam> teams = new ArrayList<>();

    private int teamsAmount = 2;

    private ScoreboardManager manager;

    private Scoreboard board;

    private Objective objective;

    private int matchTime = 2500;

    private Score scoreTime;

    private int initial_points_active = 2;

    public KOTHArena() {
        super();
        type = "KOTH";
    }

    @Override
    public void start() {
        System.out.println("Starting game " + getID() + " of type " + getType() + " with " + players.size() + " players!");

        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        objective = board.registerNewObjective("kothScore"+getID(), "dummy", "Team Scores : ");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);


        scoreTime = objective.getScore(GeneralUtils.fixColors("&fTime Left : "));
        scoreTime.setScore(matchTime / 20 / 60);

        for (Player player : players) {
            player.teleport(spawns.get((int) (Math.random() * spawns.size())));
        }
        Collections.shuffle(players);

        List<String> availableColors = new ArrayList<>(colors);
        for (int i = 0; i < teamsAmount; i++) {
            String color = availableColors.get((int) (Math.random() * availableColors.size()));
            availableColors.remove(color);
            teams.add(new KOTHTeam(color, i));
        }

        for (int i = 0; i < players.size(); i++) {
            players.get(i).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 100, true, true));
            players.get(i).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 100, true, true));
            teams.get(i % teamsAmount).addPlayer(players.get(i));
        }

        for (int i = 0; i < initial_points_active; i++) {
            activateRandomPoint();
        }


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
                    player.removePotionEffect(PotionEffectType.SLOW);
                    player.removePotionEffect(PotionEffectType.JUMP);
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
                });

            }
            startingTick++;

        } else if (status == GameStatus.IN_PROGRESS) {
            testMessageTick++;
            if (testMessageTick >= testMessageTime) {
                testMessageTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&eGame is in progress!"))));
            }
            if (matchTime % 20 == 0) {
                scoreTime.setScore(matchTime / 20- tick / 20);
            }
            if (tick - 30 >= matchTime) {
                end();
            }
            points.stream().forEach(point -> point.update());
        }
    }

    @Override
    public void endProcedure() {
        teams.stream().forEach(team -> team.deleteTeam());
        points.stream().forEach(point -> point.deactivatePoint());
        teams.clear();
        startingTick = 0;
        testMessageTick = 0;
        board.clearSlot(DisplaySlot.SIDEBAR);
        objective.unregister();
        board = null;
        objective = null;
    }

    @Override
    public void setPos(Location pos) {
        super.setPos(pos);
    }

    @Override
    public void removePlayer(Player player) {
        players.remove(player);
        teams.stream().forEach(team -> team.removePlayer(player));
    }

    @Override
    public boolean passesChecks() {
        return true;
    }

    @Override
    public void updateView(Player player) {

    }

    public int addSpawnLocation(Location location) {
        spawns.add(location);
        return spawns.size() - 1;
    }

    public int removeSpawnLocation(int id) {
        spawns.remove(id);
        return id;
    }

    public void addCapturePoint(Location location) {
        points.add(new KOTHPoint(this, location, 5));
    }

    public void addCapturePoint(Location location, int radius) {
        points.add(new KOTHPoint(this, location, radius));
    }

    public void addCapturePoint(Location location, int radius, int pointsAwarding) {
        points.add(new KOTHPoint(this, location, radius, pointsAwarding));
    }

    private void activateRandomPoint() {
        points.stream().forEach(point -> point.tickDown());
        List<KOTHPoint> availablePoints = points.stream().filter(point -> point.isActivitable()).collect(Collectors.toList());
        System.out.println("Available points: "+availablePoints.size());
        if (availablePoints.size() == 0) return;
        KOTHPoint point = availablePoints.get((int) (Math.random() * availablePoints.size()));
        point.startActivatingPoint();
    }


    class KOTHTeam {
        List<Player> players = new ArrayList<>();
        Color color;
        String colorString;
        int points = 0;
        int id;
        Team team;
        Score score;
        public KOTHTeam(String color, int id) {
            this.color = Color.fromRGB(Integer.parseInt(color.substring(0), 16));
            this.colorString = color;
            this.id = id;
            team = board.registerNewTeam("Team_" + id);
            team.setAllowFriendlyFire(false);
            score = objective.getScore(GeneralUtils.fixColors(KOTHArena.colorGarbage.get(KOTHArena.colors.indexOf(colorString))+"Team "+id+":"));
            score.setScore(0);
        }
        public void addPlayer(Player player) {
            players.add(player);
            giveItems(player);
            team.addEntry(player.getName());
            player.setScoreboard(board);
            System.out.println("Added player "+player.getName()+" to team "+id);
            player.sendMessage(GeneralUtils.fixColors(KOTHArena.colorGarbage.get(KOTHArena.colors.indexOf(colorString))+"You have joined team " + id + "!"));
        }

        public void giveItems(Player player) {
            ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
            meta.setColor(color);
            meta.setDisplayName(GeneralUtils.fixColors(KOTHArena.colorGarbage.get(KOTHArena.colors.indexOf(colorString))+"Party Hat"));
            helmet.setItemMeta(meta);
            player.getInventory().setHelmet(helmet);

            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta2 = (LeatherArmorMeta) chestplate.getItemMeta();
            meta2.setColor(color);
            meta2.setDisplayName(GeneralUtils.fixColors(KOTHArena.colorGarbage.get(KOTHArena.colors.indexOf(colorString))+"Party Vest"));
            chestplate.setItemMeta(meta2);
            player.getInventory().setChestplate(chestplate);

            ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
            LeatherArmorMeta meta3 = (LeatherArmorMeta) leggings.getItemMeta();
            meta3.setColor(color);
            meta3.setDisplayName(GeneralUtils.fixColors(KOTHArena.colorGarbage.get(KOTHArena.colors.indexOf(colorString))+"Party Pants"));
            leggings.setItemMeta(meta3);
            player.getInventory().setLeggings(leggings);

            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
            LeatherArmorMeta meta4 = (LeatherArmorMeta) boots.getItemMeta();
            meta4.setColor(color);
            meta4.setDisplayName(GeneralUtils.fixColors(KOTHArena.colorGarbage.get(KOTHArena.colors.indexOf(colorString))+"Party Shoes"));
            boots.setItemMeta(meta4);
            player.getInventory().setBoots(boots);

            ItemStack party_stick = new ItemStack(Material.STICK);
            ItemMeta meta5 = party_stick.getItemMeta();
            meta5.setDisplayName(GeneralUtils.fixColors(KOTHArena.colorGarbage.get(KOTHArena.colors.indexOf(colorString))+"Party Stick"));
            // Enchant the party stick with knockback 3
            meta5.addEnchant(Enchantment.KNOCKBACK, 3, true);
            party_stick.setItemMeta(meta5);
            player.getInventory().setItem(player.getInventory().firstEmpty(), party_stick);

            System.out.println("Added player "+player.getName()+" to team "+id);
        }
        public void removePlayer(Player player) {
            players.remove(player);
            team.removeEntry(player.getName());
            player.getInventory().clear();
            player.setScoreboard(manager.getNewScoreboard());
        }
        public void addPoint(int point) {
            points += point;
            score.setScore(points);
        }
        public void removePoint(int point) {
            points -= point;
            score.setScore(points);
        }
        public int getPoints() {
            return points;
        }
        public void setTeam(Team team) {
            this.team = team;
        }
        public Team getTeam() {
            return team;
        }
        public Color getColor() {
            return color;
        }
        public int getID() {
            return id;
        }

        public void deleteTeam() {
            for (int i = players.size() - 1; i >= 0; i--) {
                removePlayer(players.get(i));
            }
            team.unregister();
        }
    }

    public class KOTHPoint {

        private int id;

        private Location pos;

        private int radius;

        private Map<KOTHTeam,Integer> team_capture_time = new HashMap<>();

        private int capture_time = 100;

        private KOTHPointStatus status = KOTHPointStatus.INACTIVE;

        private KOTHArena arena;

        public int inactiveTime = 2;

        public int inactiveTick = 0;

        private int activatingTime = 60;

        private int activatingTick = 0;

        private int hoverTime = 60;

        private int hoverTick = 0;

        private int capturedTime = 60;

        private int capturedTick = 0;

        private boolean hoverDir = true;

        private ArmorStand bannerStand = null;

        private KOTHTeam capturedBy = null;

        private int pointsAwarding = 1;


        public KOTHPoint(KOTHArena arena, Location pos, int radius) {
            this.arena = arena;
            this.pos = pos;
            this.radius = radius;
            id = arena.points.size();
        }

        public KOTHPoint(KOTHArena arena, Location pos, int radius, int pointsAwarding) {
            this.arena = arena;
            this.pos = pos;
            this.radius = radius;
            this.pointsAwarding = pointsAwarding;
            id = points.size();
            points.add(this);
        }

        public KOTHPoint(KOTHArena arena, Location pos, int radius, int pointsAwarding, int capture_time) {
            this.arena = arena;
            this.pos = pos;
            this.radius = radius;
            this.pointsAwarding = pointsAwarding;
            this.capture_time = capture_time;
            id = points.size();
            points.add(this);
        }

        public void update() {
            if (status == KOTHPointStatus.INACTIVE) return;

            pointParticles();

            if (bannerStand != null) {

                if (hoverDir) {
                    bannerStand.teleport(bannerStand.getLocation().add(0, GeneralUtils.speedFunc(0, hoverTime, hoverTick) * 0.02, 0));
                } else {
                    bannerStand.teleport(bannerStand.getLocation().subtract(0, GeneralUtils.speedFunc(0, hoverTime, hoverTick) * 0.02, 0));
                }
                hoverTick++;
                if (hoverTick >= hoverTime) {
                    hoverTick = 0;
                    hoverDir = !hoverDir;
                }
                Player closestPlayer = null;
                double closestDistance = 1000000;
                for (Player player : arena.players) {
                    if (player.getLocation().distance(bannerStand.getLocation()) < closestDistance) {
                        closestDistance = player.getLocation().distance(bannerStand.getLocation());
                        closestPlayer = player;
                    }
                }
                if (closestPlayer != null) {
                    bannerStand.teleport(bannerStand.getLocation().setDirection(closestPlayer.getLocation().subtract(bannerStand.getLocation()).toVector()));
                }
            }

            if (status == KOTHPointStatus.ACTIVATING) {
                activatingTick++;
                if (activatingTick >= activatingTime) {
                    activatePoint();
                }
            } else if (status == KOTHPointStatus.CAPTURED) {

                ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
                LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
                meta.setColor(capturedBy.getColor());
                helmet.setItemMeta(meta);
                bannerStand.setHelmet(helmet);

                capturedTick++;
                if (capturedTick >= capturedTime) {
                    deactivatePoint();
                }
            }
            else if (status == KOTHPointStatus.ACTIVE || status == KOTHPointStatus.CAPTURING) {

                status = KOTHPointStatus.ACTIVE;
                for (Player player : arena.players) {
                    if (player.getLocation().distance(pos) <= radius && player.isSneaking()) {
                        KOTHTeam playerTeam = getPlayerTeam(player);
                        team_capture_time.put(playerTeam, team_capture_time.getOrDefault(playerTeam, 0) + 1);
                        status = KOTHPointStatus.CAPTURING;
                        int percentage = (int) ((double)team_capture_time.get(playerTeam) / (double) capture_time * 100.0);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&eCapturing point [" + generateLoadingBar(percentage) + "&e]")));
                        if (team_capture_time.get(playerTeam) >= capture_time) {
                            capturePoint(playerTeam);
                            break;
                        }
                    }
                }
            }
        }

        public void pointParticles() {
            if (status == KOTHPointStatus.INACTIVE) return;
            if (status == KOTHPointStatus.ACTIVATING) {
                players.stream().forEach(player -> player.spawnParticle(Particle.CRIT_MAGIC, pos, 1));
            }
            if (status == KOTHPointStatus.CAPTURING) {
                players.stream().forEach(player -> player.spawnParticle(Particle.CRIT, pos, 1));
            }
            if (status == KOTHPointStatus.CAPTURED) {
                players.stream().forEach(player -> player.spawnParticle(Particle.VILLAGER_HAPPY, pos, 1));
            }
            if (status == KOTHPointStatus.ACTIVE) {
                players.stream().forEach(player -> player.spawnParticle(Particle.BUBBLE_POP, pos, 1));
            }
        }

        public void tickDown() {
            if (status == KOTHPointStatus.INACTIVE) {
                if (inactiveTick > 0)
                    inactiveTick--;
            }
        }

        public void capturePoint(KOTHTeam team) {
            team.addPoint(pointsAwarding);
            status = KOTHPointStatus.CAPTURED;
            team_capture_time.clear();
            capturedBy = team;
            capturedTick = 0;
            activateRandomPoint();
        }

        public boolean startActivatingPoint() {
            if (!isActivitable()) return false;
            System.out.println("Activating point " + id);
            status = KOTHPointStatus.ACTIVATING;
            return true;
        }

        public boolean isActivitable() {
            return (status == KOTHPointStatus.INACTIVE && inactiveTick == 0);
        }

        public void activatePoint() {
            status = KOTHPointStatus.ACTIVE;
            spawnBanner();
        }

        public void deactivatePoint() {
            status = KOTHPointStatus.INACTIVE;
            if (bannerStand != null) {
                bannerStand.remove();
                bannerStand = null;
            }
            capturedBy = null;
            team_capture_time.clear();
            activatingTick = 0;
            hoverTick = 0;
            hoverDir = true;
            inactiveTick = inactiveTime;

        }

        private void spawnBanner() {
            if (bannerStand != null) {
                bannerStand.remove();
            }
            bannerStand = (ArmorStand) pos.getWorld().spawnEntity(pos.clone().add(0, 2, 0), EntityType.ARMOR_STAND);
            bannerStand.setGravity(false);
            bannerStand.setVisible(false);
            bannerStand.setBasePlate(false);

            ItemStack helmet = new ItemStack(Material.IRON_HELMET);
            bannerStand.setHelmet(helmet);
        }

        private KOTHTeam getPlayerTeam(Player player) {
            for (KOTHTeam team : arena.teams) {
                if (team.players.contains(player)) return team;
            }
            return null;
        }

        private String generateLoadingBar(int percentage) {
            String bar = "";
            for (int i = 0; i < 10; i++) {
                if (percentage >= i * 10) {
                    bar += "§a█";
                } else {
                    bar += "§c█";
                }
            }
            return bar;
        }
    }


    enum KOTHPointStatus {
        INACTIVE, ACTIVE, CAPTURING, CAPTURED, ACTIVATING
    }

}

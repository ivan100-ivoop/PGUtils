package com.github.pgutils.entities.games.kothadditionals;

import com.github.pgutils.PGUtils;
import com.github.pgutils.entities.games.KOTHArena;
import com.github.pgutils.particles.EnhancedParticle;
import com.github.pgutils.particles.variants.HollowCircleParticle;
import com.github.pgutils.particles.variants.RandomisedDirCylinderParticle;
import com.github.pgutils.particles.variants.SwirlingParticle;
import com.github.pgutils.utils.GeneralUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KOTHPoint {

    private int id;

    // Saved
    private Location pos;

    // Saved
    private double radius;

    private Map<KOTHTeam,Integer> team_capture_time = new HashMap<>();

    // Saved
    private int captureTime = 100;

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

    // Saved
    private int pointsAwarding = 1;

    private List<EnhancedParticle> activeParticles = new ArrayList<>();

    private List<EnhancedParticle> activatingParticles = new ArrayList<>();

    private List<EnhancedParticle> capturingParticles = new ArrayList<>();

    private List<EnhancedParticle> capturedParticles = new ArrayList<>();

    public KOTHPoint(KOTHArena arena, Location pos, double radius) {
        this.arena = arena;
        this.pos = pos;
        this.radius = radius;
        id = arena.getPoints().size();
        activateParticles();
    }

    public KOTHPoint(KOTHArena arena, Location pos, double radius, int pointsAwarding) {
        this.arena = arena;
        this.pos = pos;
        this.radius = radius;
        this.pointsAwarding = pointsAwarding;
        id = arena.getPoints().size();
        activateParticles();
    }

    public KOTHPoint(KOTHArena arena, Location pos, double radius, int pointsAwarding, int capturetime) {
        this.arena = arena;
        this.pos = pos;
        this.radius = radius;
        this.pointsAwarding = pointsAwarding;
        this.captureTime = capturetime;
        id = arena.getPoints().size();
        activateParticles();
    }

    public KOTHPoint() {}

    public void setup()
    {
        id = arena.getPoints().size();
        activateParticles();
    }


    public void activateParticles() {
        activeParticles.add(new SwirlingParticle(pos, radius, 0.1, 1, 0.12, Particle.TOTEM, 0) {
            @Override
            public void onUpdate() {
            }
        });
        activatingParticles.add(new SwirlingParticle(pos, radius, 0.2, 1, 0.07, Particle.CRIT, 0) {
            @Override
            public void onUpdate() {
                setRadius(getRadius() * 0.98);
                setY_offset(getY_offset() * 1.06);
                setSpeed(getSpeed() * 1.035);
            }
        });
        activatingParticles.add(new HollowCircleParticle(pos, 1.25, Particle.CRIT_MAGIC, 20, 10, 0) {
            @Override
            public void onUpdate() {
                if(getTick() > (double) activatingTime * 0.20) {
                    setY_offset(getY_offset() + ((getInitial_y_offset() - 10) - getY_offset()) * 0.1);
                    if(getTick() > (double) activatingTime * 0.80)
                        setRadius(getRadius() + ((getInitialRadius() * 3) - getRadius()) * 0.1);
                }
            }
        });

        capturingParticles.add(new SwirlingParticle(pos, radius, 0.1, 2, 0.08, Particle.FLAME, 0.1) {
            @Override
            public void onUpdate() {}
        });

        activatingParticles.add(new RandomisedDirCylinderParticle(pos.clone().add(0,10,0), 0.75, 0.1, 1, Particle.END_ROD, -0.5) {
            @Override
            public void onUpdate() {}
        });


        activatingParticles.add(new RandomisedDirCylinderParticle(pos.clone().add(0,10,0), 2, 0.5, 4, Particle.CLOUD, 0) {
            @Override
            public void onUpdate() {}
        });

        activeParticles.add(new RandomisedDirCylinderParticle(pos.clone().add(0,1,0), radius, 1.5, 2, Particle.VILLAGER_HAPPY, 0.5) {
            @Override
            public void onUpdate() {
                setActive(false);
                if (getTick() % 20 == 0) {
                    setActive(true);
                }
            }
        });
        capturedParticles.stream().forEach(particle -> particle.setOnTickReset(capturedTime));


        activatingParticles.stream().forEach(particle -> particle.setOnTickReset(activatingTime));
    }

    public void update() {
        if (status == KOTHPointStatus.INACTIVE) return;

        pointParticles();

        if (status == KOTHPointStatus.ACTIVATING) {
            activatingTick++;

            if (activatingTick >= activatingTime) {
                activatePoint();
            }
            else if (activatingTick == 0) {
                spawnBanner();
            }
            if (bannerStand != null) {
                bannerStand.teleport(bannerStand.getLocation().add(0, ((pos.getY() + 2) - bannerStand.getLocation().getY()) * 0.1, 0));
                bannerLook();
            }

        } else if (status == KOTHPointStatus.CAPTURED) {

            ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
            meta.setColor(capturedBy.getColor());
            helmet.setItemMeta(meta);
            bannerStand.setHelmet(helmet);
            if (capturedTick < capturedTime * 0.5) {
                bannerStand.teleport(bannerStand.getLocation().add(0, ((pos.getY() + 1) - bannerStand.getLocation().getY()) * 0.1, 0));
            }
            else {
                Location loc = bannerStand.getLocation().add(0, (bannerStand.getLocation().getY() - pos.getY() ) * 0.1, 0);
                bannerStand.teleport(loc);
            }

            capturedTick++;
            if (capturedTick >= capturedTime) {
                deactivatePoint();
            }

        }
        else if (status == KOTHPointStatus.ACTIVE || status == KOTHPointStatus.CAPTURING) {

            status = KOTHPointStatus.ACTIVE;
            for (Player player : arena.getPlayers()) {
                if (player.getLocation().distance(pos) <= radius && player.isSneaking()) {
                    KOTHTeam playerTeam = getPlayerTeam(player);
                    team_capture_time.put(playerTeam, team_capture_time.getOrDefault(playerTeam, 0) + 1);
                    status = KOTHPointStatus.CAPTURING;
                    int percentage = (int) ((double)team_capture_time.get(playerTeam) / (double) captureTime * 100.0);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&eCapturing point [" + GeneralUtils.generateLoadingBar(percentage, "§a", "§7") + "&e]")));
                    if (team_capture_time.get(playerTeam) >= captureTime) {
                        capturePoint(playerTeam);
                        break;
                    }
                }
            }
            updateBannerHover();
        }
    }

    public void updateBannerHover(){
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
            bannerLook();
        }
    }

    public void bannerLook()
    {
        Player closestPlayer = null;
        double closestDistance = 1000000;
        for (Player player : arena.getPlayers()) {
            if (player.getLocation().distance(bannerStand.getLocation()) < closestDistance) {
                closestDistance = player.getLocation().distance(bannerStand.getLocation());
                closestPlayer = player;
            }
        }
        if (closestPlayer != null) {
            bannerStand.teleport(bannerStand.getLocation().setDirection(closestPlayer.getLocation().subtract(bannerStand.getLocation()).toVector()));
        }
    }

    public void pointParticles() {
        if (status == KOTHPointStatus.INACTIVE) return;
        if (status == KOTHPointStatus.ACTIVATING) {
            activatingParticles.stream().forEach(particle -> particle.update());
        }
        if (status == KOTHPointStatus.CAPTURING) {
            activeParticles.stream().forEach(particle -> particle.update());
            capturingParticles.stream().forEach(particle -> particle.update());
        }
        if (status == KOTHPointStatus.CAPTURED) {
            activeParticles.stream().forEach(particle -> particle.update());

        }
        if (status == KOTHPointStatus.ACTIVE) {
            activeParticles.stream().forEach(particle -> particle.update());
        }
    }

    public void tickDown() {
        if (status == KOTHPointStatus.INACTIVE || status == KOTHPointStatus.CAPTURED) {
            if (inactiveTick > 0)
                inactiveTick--;
        }
    }

    public void capturePoint(KOTHTeam team) {
        team.addPoint(pointsAwarding);
        team.getPlayers().stream().forEach(player -> player.playSound(player,Sound.ENTITY_PLAYER_LEVELUP, 1, 0));
        Firework firework = getPosition().getWorld().spawn(getPosition(), Firework.class);

        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(1);

        FireworkEffect effect = FireworkEffect.builder()
                .withFade(team.getColor())
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build();

        meta.addEffect(effect);
        firework.setFireworkMeta(meta);
        firework.setMetadata("nodamage", new FixedMetadataValue(PGUtils.instance, true));
        new BukkitRunnable() {
            @Override
            public void run() {
                firework.detonate();
            }
        }.runTaskLater(PGUtils.instance, 1L);
        status = KOTHPointStatus.CAPTURED;
        team_capture_time.clear();
        capturedBy = team;
        capturedTick = 0;
        arena.activateRandomPoint();
        inactiveTick = inactiveTime;
    }

    public boolean startActivatingPoint() {
        if (!isActivitable()) return false;
        System.out.println("Activating point " + id);
        status = KOTHPointStatus.ACTIVATING;
        spawnBanner();
        return true;
    }

    public boolean isActivitable() {
        return (status == KOTHPointStatus.INACTIVE && inactiveTick == 0);
    }

    public void activatePoint() {
        activatingTick = 0;
        status = KOTHPointStatus.ACTIVE;
        bannerStand.teleport(pos.clone().add(0, 2, 0));

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
    }

    public void deactivatePointFull() {
        deactivatePoint();
        inactiveTick = inactiveTime;
    }

    private void spawnBanner() {
        if (bannerStand != null) {
            bannerStand.remove();
        }
        bannerStand = (ArmorStand) pos.getWorld().spawnEntity(pos.clone().add(0, 20, 0), EntityType.ARMOR_STAND);
        bannerStand.setGravity(false);
        bannerStand.setVisible(false);
        bannerStand.setBasePlate(false);

        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        bannerStand.setHelmet(helmet);
    }

    private KOTHTeam getPlayerTeam(Player player) {
        for (KOTHTeam team : arena.getTeams()) {
            if (team.getPlayers().contains(player)) return team;
        }
        return null;
    }



    public Location getPosition() {
        return pos;
    }

    public Location getLocation() {
        return pos;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getPointsAwarding() {
        return pointsAwarding;
    }

    public void setPointsAwarding(int pointsAwarding) {
        this.pointsAwarding = pointsAwarding;
    }

    public int getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(int captureTime) {
        this.captureTime = captureTime;
    }

    public void setArena(KOTHArena arena) {
        this.arena = arena;
    }

    public KOTHArena getArena() {
        return arena;
    }

    public void setLocation(Location readObject) {
        pos = readObject;
    }

    public int getID() {
        return id;
    }

    public void resetDownTime () {
        inactiveTick = 0;
    }
}
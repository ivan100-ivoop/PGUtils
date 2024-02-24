package com.github.pgutils.entities.games.kothadditionals;

import com.github.pgutils.entities.games.KOTHArena;
import com.github.pgutils.utils.GeneralUtils;
import org.bukkit.Location;

public class KOTHSpawn{

    private String uid;

    private Location pos;

    private int team_id;

    private KOTHArena arena;

    public KOTHSpawn() {
    }


    public KOTHSpawn(Location pos, int team_id, KOTHArena arena) {
        this.pos = pos;
        this.team_id = team_id;
        this.uid = GeneralUtils.generateUniqueID();
    }

    public Location getPos(){
        return pos;
    }

    public int getTeamID(){
        return team_id;
    }

    public void setPos(Location pos){
        this.pos = pos;
    }

    public void setTeamID(int team_id){
        this.team_id = team_id;
    }

    public Location getLocation() {
        return pos;
    }


    public KOTHArena getArena() {
        return arena;
    }

    public void setArena(KOTHArena kothArena) {
        this.arena = kothArena;
    }

    public String getID() {
        return uid;
    }

    public void setID(String uid) {
        this.uid = uid;
    }
}
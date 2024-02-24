package com.github.pgutils.entities.games.tntradditionals;

import com.github.pgutils.utils.GeneralUtils;
import org.bukkit.Location;

public class TNTRSpawn {

    private String uid;

    private Location pos;

    public TNTRSpawn() {
    }

    public TNTRSpawn(Location pos) {
        this.pos = pos;
        this.uid = GeneralUtils.generateUniqueID();
    }

    public Location getPos(){
        return pos;
    }

    public void setPos(Location pos){
        this.pos = pos;
    }

    public Location getLocation() {
        return pos;
    }


    public String getID() {
        return uid;
    }

    public void setID(String uid) {
        this.uid = uid;
    }
}
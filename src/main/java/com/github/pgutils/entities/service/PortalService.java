package com.github.pgutils.entities.service;

import com.github.pgutils.PGUtilsLoader;
import com.github.pgutils.utils.PortalManager;
import com.github.pgutils.utils.db.PortalSave;
import com.github.pgutils.utils.db.RespawnSave;
import org.github.icore.mysql.utils.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PortalService {
    public static Repository<UUID, PortalSave> portalRepository = PGUtilsLoader.databaseAPI.getOrCreateRepository(PortalSave.class);
    public static void getAllPortal() {
        PortalManager.portals = portalRepository.streamAllValues().collect(Collectors.toList());
    }
    public static void savePortal(PortalSave portal) {
        if(portal.getKey() == null)
            portal.setKey(UUID.randomUUID());
        portalRepository.upsert(portal);
    }
    public static void deletePortal(PortalSave portal) {
        portalRepository.delete(portal.getKey());
    }
    public static void updatePortal(PortalSave portal) {
        deletePortal(portal);
        savePortal(portal);
    }

    public static class RespawnServer {
        public Repository<UUID, RespawnSave> respawnSaveRepository = PGUtilsLoader.databaseAPI.getOrCreateRepository(RespawnSave.class);
        public RespawnSave getRespawn() {
            RespawnSave respawn = null;
            
            List<RespawnSave> respawnSaves = respawnSaveRepository.streamAllValues().collect(Collectors.toList());
            
            if(respawnSaves.size() >= 0) {
                respawn = respawnSaves.get(0);
            } else {
                respawn = new RespawnSave();
                if(respawn.getKey() == null) {
                    respawn.setKey(UUID.randomUUID());
                }
            }

            return respawn;
        }
        public void saveRespawn(RespawnSave respawnSave) {
            if(respawnSave.getKey() == null)
                respawnSave.setKey(UUID.randomUUID());
            respawnSaveRepository.upsert(respawnSave);
        }
        public void deleteRespawn(RespawnSave respawnSave) {
            if (respawnSaveRepository.get(respawnSave.getKey()) != null)
                respawnSaveRepository.delete(respawnSave.getKey());
        }
        public void updateRespawn(RespawnSave respawnSave) {
            deleteRespawn(respawnSave);
            saveRespawn(respawnSave);
        }
    }
}

package com.github.pgutils.entities.service;

import com.github.pgutils.PGUtilsLoader;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.db.LobbySave;
import org.github.icore.mysql.utils.Promise;
import org.github.icore.mysql.utils.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LobbyService {
    public static Repository<UUID, LobbySave> lobbyRepository = PGUtilsLoader.databaseAPI.getOrCreateRepository(LobbySave.class);
    public static void getAllLobbies() {
        for (LobbySave lobbySave: lobbyRepository.streamAllValues().collect(Collectors.toList())){
            Lobby _lobby = new Lobby();

            _lobby.setMode(lobbySave.getMode());
            _lobby.setLock(lobbySave.isLocked());
            _lobby.setTournamentMode(lobbySave.isTournamentMode());
            _lobby.setName(lobbySave.getName());
            _lobby.setMaxPlayers(lobbySave.getMaxPlayers());
            _lobby.setMinPlayers(lobbySave.getMinPlayers());
            _lobby.setUID(lobbySave.getKey().toString());
            _lobby.setPos(lobbySave.getLocation());
        }

    }
    public static void saveLobby(Lobby lobby) {
        LobbySave _lobby = new LobbySave()
                .setTournamentMode(lobby.isTournament())
                .setMode(lobby.getMode())
                .setName(lobby.getName())
                .setMinPlayers(lobby.getMinPlayers())
                .setMaxPlayers(lobby.getMaxPlayers())
                .setLocation(lobby.getLocation())
                .setLocked(lobby.isLocked());

        _lobby.setKey(UUID.fromString(lobby.getUID()));

        lobbyRepository.upsert(_lobby);
    }
    public static void deleteLobby(Lobby lobby) {
        lobbyRepository.delete(UUID.fromString(lobby.getUID()));
    }
    public static void updateLobby(Lobby lobby) {
        deleteLobby(lobby);
        saveLobby(lobby);
    }
}

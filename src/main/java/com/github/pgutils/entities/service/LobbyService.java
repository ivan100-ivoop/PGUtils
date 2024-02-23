package com.github.pgutils.entities.service;

import com.github.pgutils.PGUtilsLoader;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.utils.GeneralUtils;
import org.github.icore.mysql.utils.Promise;
import org.github.icore.mysql.utils.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LobbyService {

    public static Repository<String, Lobby> lobbyRepository = PGUtilsLoader.databaseAPI.getOrCreateRepository(Lobby.class);

    public static List<Lobby> getAllLobbies() {
        return lobbyRepository.streamAllValues().collect(Collectors.toList());
    }

    public static Promise.AsyncEmptyResult saveLobby(Lobby lobby) {
        lobby.setKey(GeneralUtils.generateUniqueID());
        return lobbyRepository.insertAsync(lobby);
    }

    public static Promise.AsyncEmptyResult deleteLobby(Lobby lobby) {
        return lobbyRepository.deleteAsync(lobby);
    }

    public static Promise.AsyncEmptyResult updateLobby(Lobby lobby) {
        return lobbyRepository.updateAsync(lobby);
    }
}

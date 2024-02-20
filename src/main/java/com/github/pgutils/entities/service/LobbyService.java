package com.github.pgutils.entities.service;

import com.github.pgutils.entities.Lobby;
import com.nivixx.ndatabase.api.NDatabase;
import com.nivixx.ndatabase.api.Promise;
import com.nivixx.ndatabase.api.repository.Repository;

import java.util.List;
import java.util.stream.Collectors;

public class LobbyService {

    public static List<Lobby> getAllLobbies() {
        Repository<String, Lobby> lobbyRepository = NDatabase.api().getOrCreateRepository(Lobby.class);
        return lobbyRepository.streamAllValues().collect(Collectors.toList());
    }

    public static Promise.AsyncEmptyResult saveLobby(Lobby lobby) {
        Repository<String, Lobby> lobbyRepository = NDatabase.api().getOrCreateRepository(Lobby.class);
        return lobbyRepository.insertAsync(lobby);
    }

    public static Promise.AsyncEmptyResult deleteLobby(Lobby lobby) {
        Repository<String, Lobby> lobbyRepository = NDatabase.api().getOrCreateRepository(Lobby.class);
        return lobbyRepository.deleteAsync(lobby);
    }

    public static Promise.AsyncEmptyResult updateLobby(Lobby lobby) {
        Repository<String, Lobby> lobbyRepository = NDatabase.api().getOrCreateRepository(Lobby.class);
        return lobbyRepository.updateAsync(lobby);
    }
}

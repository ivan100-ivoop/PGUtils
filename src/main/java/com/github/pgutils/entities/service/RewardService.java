package com.github.pgutils.entities.service;

import com.github.pgutils.PGUtilsLoader;
import com.github.pgutils.utils.RewardManager;
import com.github.pgutils.utils.db.RewardSave;
import org.github.icore.mysql.utils.Repository;

import java.util.UUID;
import java.util.stream.Collectors;

public class RewardService {
    public static Repository<UUID, RewardSave> rewardRepository = PGUtilsLoader.databaseAPI.getOrCreateRepository(RewardSave.class);
    public static void getAllRewards() {
        RewardManager.rewards = rewardRepository.streamAllValues().collect(Collectors.toList());
    }
    public static void saveRewards(RewardSave reward) {
        if(reward.getKey() == null)
            reward.setKey(UUID.randomUUID());
        rewardRepository.upsert(reward);
    }
    public static void deleteRewards(RewardSave reward) {
        rewardRepository.delete(reward.getKey());
    }
}

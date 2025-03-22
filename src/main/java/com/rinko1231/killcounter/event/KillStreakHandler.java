package com.rinko1231.killcounter.event;

import com.rinko1231.killcounter.KillData;
import com.rinko1231.killcounter.config.KillCounterConfig;
import com.rinko1231.killcounter.network.KillStreakNetwork;
import com.rinko1231.killcounter.network.KillStreakPayload;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class KillStreakHandler {
    private static final Map<UUID, KillData> playerKills = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if(KillCounterConfig.enableKillCounter.get()) {
            if (event.getEntity() instanceof Player victim && event.getSource().getEntity() != null) {
                UUID victimId = victim.getUUID();
                long currentTime = System.currentTimeMillis();
                KillData data = playerKills.computeIfAbsent(victimId, k -> new KillData());
                long victimTimeDiff = currentTime - data.getLastKillTime();
                if (victimTimeDiff < KillCounterConfig.streakKillTime.get()) {
                    if (data.getCurrentStreak() >= 2)
                        broadcastShutDownOverServer(victim);
                }

                playerKills.remove(victimId);
            }

            if (event.getSource().getEntity() instanceof Player player) {
                if (shouldCountEntity(event.getEntity()) && event.getSource().getEntity() != event.getEntity()) {
                    handlePlayerKill(player);
                }
            }
        }

    }

    private boolean shouldCountEntity(LivingEntity entity) {
        String entityID = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
        if(KillCounterConfig.enemyBlackOrWhiteList.get())
            return !KillCounterConfig.enemyBlacklist.get().contains(entityID);
        else
            return KillCounterConfig.enemyWhitelist.get().contains(entityID);
    }

    private void handlePlayerKill(Player player) {
        UUID playerId = player.getUUID();
        long currentTime = System.currentTimeMillis();

        KillData data = playerKills.computeIfAbsent(playerId, k -> new KillData());

        long timeDiff = currentTime - data.getLastKillTime();

        if (timeDiff > KillCounterConfig.streakKillTime.get()) {
            resetStreak(data);
            sendKillMessage(player, data, timeDiff);
            data.update(currentTime);
        } else {
            updateStreak(data, timeDiff);
            sendKillMessage(player, data, timeDiff);
            data.updateTimeOnly(currentTime);
        }

    }

    private void resetStreak(KillData data) {
        data.setCurrentStreak(1);
        data.setQuickKills(1);
    }

    private void updateStreak(KillData data, long timeDiff) {
        if (timeDiff < KillCounterConfig.quickKillTime.get()) {
            data.setQuickKills(data.getQuickKills() + 1);
        } else {
            data.setQuickKills(1);
        }
        data.setCurrentStreak(data.getCurrentStreak() + 1);
    }


    private void sendKillMessage(Player player, KillData data, long timeDiff) {
        int quick = data.getQuickKills();
        int total = data.getCurrentStreak();

        // 发送快速击杀消息
        if (timeDiff < KillCounterConfig.quickKillTime.get() && quick >= 2 && quick <= 5) {
            broadcastOverServer(player, "quick", quick);
        }
        // 发送总击杀消息
        if (total > 2) {
            int sendTotal = Math.min(total, 8);
            broadcastOverServer(player, "total", sendTotal);
        }


    }

    private void sendNetworkMessage(Player player, String type, int streak) {
        if (player instanceof ServerPlayer serverPlayer) {

            KillStreakNetwork.send(
                    serverPlayer,
                    new KillStreakPayload(
                            type,
                            streak,
                            player.getName().getString()
                    )
            );

        }
    }

    private void broadcastOverServer(Player killer, String type, int streak) {

        Objects.requireNonNull(killer.getServer())
                .getPlayerList()
                .getPlayers()
                .forEach(player ->
                        sendNetworkMessage(killer,type,streak)
                );

    }

    private void broadcastShutDownOverServer(Player victim) {

        Objects.requireNonNull(victim.getServer())
                .getPlayerList()
                .getPlayers()
                .forEach(player ->
                        broadcastShutDown(victim)
                );

    }

    private void broadcastShutDown(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            KillStreakNetwork.send(
                    serverPlayer,
                    new KillStreakPayload(
                            "shutdown",
                            0,
                            player.getName().getString()
                    )
            );
        }
    }


}

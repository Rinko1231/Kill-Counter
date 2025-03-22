package com.rinko1231.killcounter.event;

import com.rinko1231.killcounter.KillData;
import com.rinko1231.killcounter.config.KillCounterConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class KillStreakHandler {
    private static final Map<UUID, KillData> playerKills = new ConcurrentHashMap<>();
    //private static final long SHORT_TIME = KillCounterConfig.quickKillTime.get();
    //private static final long MAX_TIME = KillCounterConfig.streakKillTime.get();


    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if(event.getEntity() instanceof Player victim && event.getSource().getEntity()!=null)
        {
            UUID victimId = victim.getUUID();
            long currentTime = System.currentTimeMillis();
            KillData data = playerKills.computeIfAbsent(victimId, k -> new KillData());
            long victimTimeDiff = currentTime - data.getLastKillTime();
            if (victimTimeDiff < KillCounterConfig.streakKillTime.get()) {
                if(data.getCurrentStreak() >= 2)
                    broadcastShutDown(victim);
            }

            playerKills.remove(victimId);
        }

        if (event.getSource().getEntity() instanceof Player player) {
            if (shouldCountEntity(event.getEntity()) && event.getSource().getEntity() != event.getEntity()){
                handlePlayerKill(player);
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

    private static final Map<Integer, String> QUICK_KILL_MESSAGES = new HashMap<>();
    private static final Map<Integer, String> TOTAL_KILL_MESSAGES = new HashMap<>();

    static {
        QUICK_KILL_MESSAGES.put(2, "killcounter.quick.double");
        QUICK_KILL_MESSAGES.put(3, "killcounter.quick.triple");
        QUICK_KILL_MESSAGES.put(4, "killcounter.quick.quadra");
        QUICK_KILL_MESSAGES.put(5, "killcounter.quick.penta");


        TOTAL_KILL_MESSAGES.put(3, "killcounter.total.three");//大杀特杀
        TOTAL_KILL_MESSAGES.put(4, "killcounter.total.four");//主宰比赛
        TOTAL_KILL_MESSAGES.put(5, "killcounter.total.five");//暴走
        TOTAL_KILL_MESSAGES.put(6, "killcounter.total.six");//不可阻挡
        TOTAL_KILL_MESSAGES.put(7, "killcounter.total.seven");//接近神了
        TOTAL_KILL_MESSAGES.put(8, "killcounter.total.eight");//超神
    }

    private void sendKillMessage(Player player, KillData data, long timeDiff) {
        int quick = data.getQuickKills();
        int total = data.getCurrentStreak();


        MutableComponent combinedMessage = null;

        if (total > 2) {

            String totalKey = TOTAL_KILL_MESSAGES.getOrDefault(
                    Math.min(total, 8),
                    "killcounter.total.eight" // 超过8次继续使用Legendary
            );
            combinedMessage = Component.translatable(
                    totalKey,
                    player.getName()
            );
        }


        if (timeDiff < KillCounterConfig.quickKillTime.get() && quick >= 2 && quick <= 5) {
            String quickKey = QUICK_KILL_MESSAGES.get(quick);
            MutableComponent quickMessage = Component.translatable(quickKey,player.getName());

            if (combinedMessage != null) {
                combinedMessage = Component.translatable("killcounter.combined",
                        quickMessage,
                        combinedMessage
                );
            } else {
                combinedMessage = quickMessage;
            }
        }

        if (combinedMessage != null) {
            Objects.requireNonNull(player.getServer()).getPlayerList().broadcastSystemMessage(combinedMessage, false);
        }

    }

    private void broadcastShutDown(Player player)
    {
       MutableComponent shutDownMessage = Component.translatable("killcounter.shutdown",
                player.getName()
        );
       Objects.requireNonNull(player.getServer()).getPlayerList().broadcastSystemMessage(shutDownMessage, false);

    }

}

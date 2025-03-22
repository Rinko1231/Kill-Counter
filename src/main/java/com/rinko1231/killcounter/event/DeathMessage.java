package com.rinko1231.killcounter.event;

import com.rinko1231.killcounter.config.KillCounterConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.Objects;

public class DeathMessage {


    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (hasOwner(entity)) return;
        if (entity instanceof Player) return;
        if (KillCounterConfig.deathMsgBlackOrWhiteList.get()) {//判断黑白名单
            if (KillCounterConfig.deathMsgBlacklist.get().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString()))
                return;//黑名单包含则拒绝
        } else {
            if (!KillCounterConfig.deathMsgWhitelist.get().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString()))
                return;//白名单不包含则拒绝
        }

        DamageSource source = event.getSource();
        if (source.getEntity() instanceof Player killer) {
            handleKillMessage(killer, entity);
        }
    }

    private boolean hasOwner(LivingEntity entity) {
        // 实现检查生物是否有主人的逻辑
        if (entity instanceof TamableAnimal tamable) {
            return tamable.isTame() && tamable.getOwner() != null;
        }
        // 添加其他可能有主人的生物类型检查
        return false;
    }

    private void handleKillMessage(Player killer, LivingEntity victim) {
        Component deathMessage = victim.getCombatTracker().getDeathMessage();
        Objects.requireNonNull(killer.getServer()).getPlayerList().broadcastSystemMessage(deathMessage, false);
    }


}

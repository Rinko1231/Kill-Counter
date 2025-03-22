package com.rinko1231.killcounter.client;

import com.rinko1231.killcounter.KillCounter;
import com.rinko1231.killcounter.config.KillClientConfig;
import com.rinko1231.killcounter.network.DeathMessageNetwork;
import com.rinko1231.killcounter.network.KillStreakNetwork;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = KillCounter.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void onPayloadRegister(RegisterPayloadHandlersEvent event) {
        // 注册客户端网络处理器
        KillStreakNetwork.register(event);
        DeathMessageNetwork.register(event);
    }

}

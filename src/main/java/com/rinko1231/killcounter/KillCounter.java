package com.rinko1231.killcounter;



import com.rinko1231.killcounter.config.KillClientConfig;
import com.rinko1231.killcounter.config.KillCounterConfig;
import com.rinko1231.killcounter.event.DeathMessage;
import com.rinko1231.killcounter.event.KillStreakHandler;
import com.rinko1231.killcounter.network.DeathMessageNetwork;
import com.rinko1231.killcounter.network.KillStreakNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(KillCounter.MODID)
public class KillCounter {
    public static final String MODID = "killcounter";

    // 添加这行 ↓
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public KillCounter(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, KillCounterConfig.CONFIG);
        modContainer.registerConfig(ModConfig.Type.CLIENT, KillClientConfig.CLIENT_SPEC);

        // 注册服务端事件处理器
        NeoForge.EVENT_BUS.register(new DeathMessage());
        NeoForge.EVENT_BUS.register(new KillStreakHandler());
    }

    // 添加网络注册入口
    @SubscribeEvent
    public void onCommonSetup(RegisterPayloadHandlersEvent event) {
        KillStreakNetwork.register(event);
        DeathMessageNetwork.register(event);
    }
}

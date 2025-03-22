package com.rinko1231.killcounter;



import com.rinko1231.killcounter.config.KillCounterConfig;
import com.rinko1231.killcounter.event.DeathMessage;
import com.rinko1231.killcounter.event.KillStreakHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod("killcounter")
public class KillCounter {
    public static final String MODID = "killcounter";

    public KillCounter(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, KillCounterConfig.CONFIG);
        NeoForge.EVENT_BUS.register(new DeathMessage());
        NeoForge.EVENT_BUS.register(new KillStreakHandler());

    }

}

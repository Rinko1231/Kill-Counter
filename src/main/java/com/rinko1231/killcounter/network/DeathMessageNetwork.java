package com.rinko1231.killcounter.network;

import com.rinko1231.killcounter.KillCounter;
import com.rinko1231.killcounter.config.KillClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

public class DeathMessageNetwork {
    public static void register(final RegisterPayloadHandlersEvent event) {
        event.registrar(KillCounter.MODID)
                .versioned("1.0.0")
                .playToClient(
                        DeathMessagePayload.TYPE,
                        DeathMessagePayload.STREAM_CODEC,
                        DeathMessageNetwork::handleClientMessage
                );
    }

    private static void handleClientMessage(DeathMessagePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow() == PacketFlow.CLIENTBOUND) {
                handleOnClient(payload);
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleOnClient(DeathMessagePayload payload) {
        if (!KillClientConfig.CLIENT.showDeathMessage.get()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        try {
            Component message = deserializeComponent(payload.jsonData());
            mc.gui.getChat().addMessage(message);
        } catch (Exception e) {
            KillCounter.LOGGER.error("Failed to parse death message: {}", payload.jsonData(), e);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static Component deserializeComponent(String json) {
        return Component.Serializer.fromJson(json, Objects.requireNonNull(Minecraft.getInstance().getConnection()).registryAccess());
    }

    // 服务端发送方法
    public static void send(ServerPlayer player, Component message) {
        String json = serializeComponent(message, player.registryAccess());
        PacketDistributor.sendToPlayer(player, new DeathMessagePayload(json));
    }

    private static String serializeComponent(Component component, HolderLookup.Provider registries) {
        return Component.Serializer.toJson(component, registries);
    }
}

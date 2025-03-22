package com.rinko1231.killcounter.network;

import com.rinko1231.killcounter.config.KillClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class KillStreakNetwork {
        public static void register(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToClient(
                    KillStreakPayload.TYPE,
                    KillStreakPayload.STREAM_CODEC,
                    KillStreakNetwork::handleClientMessage
            );

        }


        private static void handleClientMessage(KillStreakPayload payload, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (KillClientConfig.CLIENT.showKillStreaks.get()) {
                    Component message = switch (payload.messageType()) {
                        case "quick" -> getQuickMessage(payload);
                        case "total" -> getTotalMessage(payload);
                        case "shutdown" -> getShutdownMessage(payload);
                        default -> Component.literal("");
                    };

                    Minecraft.getInstance().gui.getChat().addMessage(message);
                }
            });
        }

        private static Component getQuickMessage(KillStreakPayload payload) {
            return Component.translatable(
                    "killcounter.quick." + payload.streakCount(),
                    payload.playerName()
            );
        }

        private static Component getTotalMessage(KillStreakPayload payload) {
            String key = "killcounter.total." + Math.min(payload.streakCount(), 8);
            return Component.translatable(key, payload.playerName());
        }

        private static Component getShutdownMessage(KillStreakPayload payload) {
            return Component.translatable("killcounter.shutdown", payload.playerName());
        }

        public static void send(ServerPlayer player, KillStreakPayload payload) {
            PacketDistributor.sendToPlayer(player, payload);

    }



}

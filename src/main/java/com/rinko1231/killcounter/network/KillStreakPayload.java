package com.rinko1231.killcounter.network;



import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record KillStreakPayload(String messageType, int streakCount, String playerName)
        implements CustomPacketPayload {

    public static final Type<KillStreakPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("killcounter", "killstreak"));

    public static final StreamCodec<ByteBuf, KillStreakPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    KillStreakPayload::messageType,
                    ByteBufCodecs.VAR_INT,
                    KillStreakPayload::streakCount,
                    ByteBufCodecs.STRING_UTF8,
                    KillStreakPayload::playerName,
                    KillStreakPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

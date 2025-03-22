package com.rinko1231.killcounter.network;

import com.rinko1231.killcounter.KillCounter;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record DeathMessagePayload(String jsonData) implements CustomPacketPayload {
    public static final Type<DeathMessagePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(KillCounter.MODID, "deathmsg"));

    public static final StreamCodec<ByteBuf, DeathMessagePayload> STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(DeathMessagePayload::new, DeathMessagePayload::jsonData);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

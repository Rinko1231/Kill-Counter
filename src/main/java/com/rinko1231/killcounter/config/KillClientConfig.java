package com.rinko1231.killcounter.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class KillClientConfig {

    // 客户端配置
    public static final ModConfigSpec CLIENT_SPEC;
    public static final ClientConfig CLIENT;

    static {

        // 客户端配置
        final Pair<ClientConfig, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = clientPair.getRight();
        CLIENT = clientPair.getLeft();
    }


    public static class ClientConfig {
        public final ModConfigSpec.BooleanValue showKillStreaks;
        public final ModConfigSpec.BooleanValue showDeathMessage;

        public ClientConfig(ModConfigSpec.Builder builder) {
            builder.push("client");
            showKillStreaks = builder
                    .comment("Display kill streak messages")
                    .define("show Streak Messages", true);
            showDeathMessage = builder
                    .comment("Display death messages")
                    .define("show Death Messages", true);

            builder.pop();
        }
    }
}

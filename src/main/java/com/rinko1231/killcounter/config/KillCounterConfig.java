package com.rinko1231.killcounter.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class KillCounterConfig {

    public static ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static ModConfigSpec CONFIG;
    public static ModConfigSpec.LongValue quickKillTime;
    public static ModConfigSpec.LongValue streakKillTime;
    public static ModConfigSpec.BooleanValue enemyBlackOrWhiteList;
    public static ModConfigSpec.ConfigValue<List<? extends String>> enemyBlacklist;
    public static ModConfigSpec.ConfigValue<List<? extends String>> enemyWhitelist;
    public static ModConfigSpec.BooleanValue deathMsgBlackOrWhiteList;
    public static ModConfigSpec.ConfigValue<List<? extends String>> deathMsgBlacklist;
    public static ModConfigSpec.ConfigValue<List<? extends String>> deathMsgWhitelist;


    static
    {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        BUILDER.push("Kill Counter Config");

        streakKillTime = BUILDER
                .comment("Maximum duration to maintain kill streak (e.g. killing spree, legendary) (in milliseconds)")
                .defineInRange("streak Kill Time",180000,1, Long.MAX_VALUE);
        quickKillTime = BUILDER
                .comment("Time window for quick kills (e.g. double kill, penta kill) (in milliseconds)")
                .defineInRange("quick Kill Time",20000,1, Long.MAX_VALUE);

        enemyBlackOrWhiteList = BUILDER
                .comment("Enable blacklist or whitelist for entities that can trigger kill counter.")
                .comment("'true' means blacklist")
                .define("Entity Blacklist Or WhiteList",true);

        enemyBlacklist = BUILDER
                .comment("Blacklist for entities that can trigger kill counter.")
                .defineList("Entity Blacklist", List.of(
                        "minecraft:armor_stand",
                        "minecraft:slime"), () -> "", o -> (o instanceof String));
        enemyWhitelist = BUILDER
                .comment("Whitelist for entities that can trigger kill counter.")
                .defineList("Entity Whitelist", List.of(
                        "minecraft:player",
                        "modC:monsterD"), () -> "", o -> (o instanceof String));

        deathMsgBlackOrWhiteList = BUILDER
                .comment("Enable blacklist or whitelist for entities that can trigger death message.")
                .comment("'true' means blacklist")
                .define("Death Msg Blacklist Or WhiteList",true);

        deathMsgBlacklist = BUILDER
                .comment("Blacklist for entities that can trigger death message.")
                .defineList("Death Msg Blacklist", List.of(
                        "minecraft:armor_stand",
                        "minecraft:slime"), () -> "", o -> (o instanceof String));
        deathMsgWhitelist = BUILDER
                .comment("Whitelist for entities that can trigger death message.")
                .defineList("Death Msg Whitelist", List.of(
                        "minecraft:player",
                        "modC:monsterD"), () -> "", o -> (o instanceof String));

        CONFIG = BUILDER.build();
    }
}

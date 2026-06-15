package com.xybakaqaq.hanabiremake;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "hanabiremake", useMetadata = true)
public class Hanabi {
    public static Hanabi INSTANCE;
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        INSTANCE = this;
        System.out.println("[Hanabi] Client initialized.");
    }
}

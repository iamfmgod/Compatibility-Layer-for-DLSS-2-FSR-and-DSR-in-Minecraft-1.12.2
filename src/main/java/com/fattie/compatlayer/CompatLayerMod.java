package com.fattie.compatlayer;

import com.fattie.compatlayer.client.hud.UpscalerHUDOverlay;
import com.fattie.compatlayer.client.input.KeybindHandler;
import com.fattie.compatlayer.command.DLSSCommand;
import com.fattie.compatlayer.config.CompatConfig;
import com.fattie.compatlayer.nativebridge.DLSSBridge;
import com.fattie.compatlayer.nativebridge.FSRBridge;
import com.fattie.compatlayer.nativebridge.NativeLoader;
import com.fattie.compatlayer.util.PerformanceLogger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;

import java.io.File;

@Mod(modid = CompatLayerMod.MODID, name = "CompatLayer", version = "1.0", clientSideOnly = true)
public class CompatLayerMod {
    public static final String MODID = "compatlayer";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("[CompatLayer] Pre-initializing...");
        CompatConfig.load(new File(event.getModConfigurationDirectory(), "compatlayer.cfg"));
        NativeLoader.load();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("[CompatLayer] Initializing...");
        KeybindHandler.register();
        MinecraftForge.EVENT_BUS.register(new KeybindHandler());
        MinecraftForge.EVENT_BUS.register(new UpscalerHUDOverlay());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[CompatLayer] Shutting down...");
            PerformanceLogger.shutdown();
            try {
                DLSSBridge.shutdown();
                FSRBridge.shutdown();
            } catch (Throwable t) {
                System.err.println("[CompatLayer] Native shutdown failed: " + t.getMessage());
            }
        }));
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new DLSSCommand());
    }
}
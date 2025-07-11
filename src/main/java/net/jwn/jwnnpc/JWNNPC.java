package net.jwn.jwnnpc;

import com.mojang.logging.LogUtils;
import net.jwn.jwnnpc.block.ModBlocks;
import net.jwn.jwnnpc.item.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(JWNNPC.MODID)
public class JWNNPC {
    public static final String MODID = "jwnnpc";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JWNNPC(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);


        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);


        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}

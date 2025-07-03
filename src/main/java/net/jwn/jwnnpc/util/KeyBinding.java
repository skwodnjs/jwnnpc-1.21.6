package net.jwn.jwnnpc.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.jwn.jwnnpc.JWNNPC;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {
    public static final String CATEGORY_TEST_ID = "key.category." + JWNNPC.MODID + ".tutorial";
    public static final String TEST_KEY_ID = "key." + JWNNPC.MODID + ".drink_water";

    public static final KeyMapping TEST_KEY = new KeyMapping(TEST_KEY_ID, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY_TEST_ID);

    @EventBusSubscriber
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBinding.TEST_KEY);
        }
    }
}

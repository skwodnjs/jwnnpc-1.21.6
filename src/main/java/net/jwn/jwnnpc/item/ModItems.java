package net.jwn.jwnnpc.item;

import net.jwn.jwnnpc.JWNNPC;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JWNNPC.MODID);



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

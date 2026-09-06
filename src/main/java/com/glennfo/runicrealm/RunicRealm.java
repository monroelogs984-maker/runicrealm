package com.glennfo.runicrealm;

import com.glennfo.runicrealm.item.RunicRealmCreativeTab;
import com.glennfo.runicrealm.item.RunicRealmItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RunicRealm.MODID)
public class RunicRealm {
    public static final String MODID = "runicrealm";

    public RunicRealm() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        RunicRealmItems.ITEMS.register(bus);
        RunicRealmCreativeTab.TABS.register(bus);
    }
}

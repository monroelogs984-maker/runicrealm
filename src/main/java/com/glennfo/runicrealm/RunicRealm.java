package com.glennfo.runicrealm;

import com.glennfo.runicrealm.block.RunicRealmBlocks;
import com.glennfo.runicrealm.client.RunicRealmClient;
import com.glennfo.runicrealm.item.RunicRealmCreativeTab;
import com.glennfo.runicrealm.item.RunicRealmItems;
import com.glennfo.runicrealm.worldgen.RunicRealmFeatures;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RunicRealm.MODID)
public class RunicRealm {
    public static final String MODID = "runicrealm";

    public RunicRealm() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        RunicRealmBlocks.BLOCKS.register(bus);
        RunicRealmItems.ITEMS.register(bus);
        RunicRealmCreativeTab.TABS.register(bus);
        RunicRealmFeatures.FEATURES.register(bus);
        bus.addListener(RunicRealmClient::onClientSetup);
    }
}

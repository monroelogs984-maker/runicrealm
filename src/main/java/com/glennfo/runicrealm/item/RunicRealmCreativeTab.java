package com.glennfo.runicrealm.item;

import com.glennfo.runicrealm.RunicRealm;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class RunicRealmCreativeTab {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RunicRealm.MODID);

    public static final RegistryObject<CreativeModeTab> TAB =
            TABS.register(RunicRealm.MODID, () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + RunicRealm.MODID))
                    .icon(() -> new ItemStack(RunicRealmItems.SOUL_AND_STEEL.get()))
                    .displayItems((params, output) ->
                            RunicRealmItems.ITEMS.getEntries().forEach(item -> output.accept(item.get())))
                    .build());

    private RunicRealmCreativeTab() {}
}

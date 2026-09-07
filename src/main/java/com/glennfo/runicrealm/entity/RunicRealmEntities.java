package com.glennfo.runicrealm.entity;

import com.glennfo.runicrealm.RunicRealm;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RunicRealmEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RunicRealm.MODID);

    // Dimensions/tracking match vanilla EntityType.SKELETON exactly.
    public static final RegistryObject<EntityType<RunicMinerSkeleton>> MINER_SKELETON =
            ENTITY_TYPES.register("miner_skeleton", () -> EntityType.Builder
                    .of(RunicMinerSkeleton::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8)
                    .build("miner_skeleton"));

    public static final RegistryObject<EntityType<Firefly>> FIREFLY =
            ENTITY_TYPES.register("firefly", () -> EntityType.Builder
                    .of(Firefly::new, MobCategory.AMBIENT)
                    .sized(0.3F, 0.3F)
                    .clientTrackingRange(6)
                    .build("firefly"));

    // AbstractSkeleton.createAttributes() is vanilla's own skeleton attribute builder -
    // reused directly so health/speed/etc. match a normal skeleton exactly.
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(MINER_SKELETON.get(), AbstractSkeleton.createAttributes().build());
        event.put(FIREFLY.get(), Firefly.createAttributes().build());
    }

    private RunicRealmEntities() {}
}

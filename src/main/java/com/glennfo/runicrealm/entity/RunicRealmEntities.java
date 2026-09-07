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

    // Dimensions match each mob's real vanilla base class exactly.
    public static final RegistryObject<EntityType<SpeleothemGuardian>> SPELEOTHEM_GUARDIAN =
            ENTITY_TYPES.register("speleothem_guardian", () -> EntityType.Builder
                    .of(SpeleothemGuardian::new, MobCategory.MONSTER)
                    .sized(1.4F, 2.7F)
                    .clientTrackingRange(10)
                    .build("speleothem_guardian"));

    public static final RegistryObject<EntityType<RiftStalker>> RIFT_STALKER =
            ENTITY_TYPES.register("rift_stalker", () -> EntityType.Builder
                    .of(RiftStalker::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("rift_stalker"));

    public static final RegistryObject<EntityType<CorruptedWisp>> CORRUPTED_WISP =
            ENTITY_TYPES.register("corrupted_wisp", () -> EntityType.Builder
                    .of(CorruptedWisp::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.8F)
                    .clientTrackingRange(8)
                    .build("corrupted_wisp"));

    public static final RegistryObject<EntityType<CorruptedArmor>> CORRUPTED_ARMOR =
            ENTITY_TYPES.register("corrupted_armor", () -> EntityType.Builder
                    .of(CorruptedArmor::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("corrupted_armor"));

    public static final RegistryObject<EntityType<PoolLurker>> POOL_LURKER =
            ENTITY_TYPES.register("pool_lurker", () -> EntityType.Builder
                    .of(PoolLurker::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.3F)
                    .clientTrackingRange(8)
                    .build("pool_lurker"));

    // AbstractSkeleton.createAttributes() is vanilla's own skeleton attribute builder -
    // reused directly so health/speed/etc. match a normal skeleton exactly.
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(MINER_SKELETON.get(), AbstractSkeleton.createAttributes().build());
        event.put(FIREFLY.get(), Firefly.createAttributes().build());
        event.put(SPELEOTHEM_GUARDIAN.get(), SpeleothemGuardian.createAttributes().build());
        event.put(RIFT_STALKER.get(), RiftStalker.createAttributes().build());
        event.put(CORRUPTED_WISP.get(), CorruptedWisp.createAttributes().build());
        event.put(CORRUPTED_ARMOR.get(), CorruptedArmor.createAttributes().build());
        event.put(POOL_LURKER.get(), PoolLurker.createAttributes().build());
    }

    private RunicRealmEntities() {}
}

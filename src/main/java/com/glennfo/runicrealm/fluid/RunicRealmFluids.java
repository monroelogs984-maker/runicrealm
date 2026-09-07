package com.glennfo.runicrealm.fluid;

import com.glennfo.runicrealm.RunicRealm;
import com.glennfo.runicrealm.block.RunicRealmBlocks;
import com.glennfo.runicrealm.item.RunicRealmItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

/**
 * Bioluminescent Water: same FluidType.Properties as vanilla water (verified
 * via javap against ForgeMod's own water registration - fallDistanceModifier
 * 0, canExtinguish/canConvertToSource/supportsBoating/canHydrate true, the 3
 * bucket/vaporize sounds; canSwim/canDrown/motionScale/density/viscosity are
 * left at FluidType's own defaults, same as vanilla water itself does) plus
 * a light level, so it's genuinely "standard water physics" with a glow.
 *
 * Every fluid<->block<->bucket cross-reference below is wrapped in an
 * explicit () -> X.get() lambda rather than a bare method reference -
 * RunicRealmFluids, RunicRealmBlocks, and RunicRealmItems all need each
 * other's RegistryObjects, and a bare reference would force whichever class
 * loads second to read a static field of the first mid-initialization
 * (returning null, not the eventual value). A lambda body isn't evaluated
 * until actually invoked, well after all three classes have finished
 * loading, which sidesteps the ordering problem entirely.
 */
public final class RunicRealmFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, RunicRealm.MODID);
    public static final DeferredRegister<net.minecraft.world.level.material.Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, RunicRealm.MODID);

    // Placeholder look: vanilla's own still/flow water textures, tinted a cyan-teal glow so
    // it's visually distinct in the meantime - Glenn's building the real texture separately.
    private static final ResourceLocation STILL_TEXTURE = new ResourceLocation("minecraft", "block/water_still");
    private static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation("minecraft", "block/water_flow");
    private static final int TINT_COLOR = 0xFF3FE4C8;

    public static final RegistryObject<FluidType> BIOLUMINESCENT_WATER_TYPE = FLUID_TYPES.register(
            "bioluminescent_water", () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("block.runicrealm.bioluminescent_water")
                    .fallDistanceModifier(0.0F)
                    .canExtinguish(true)
                    .canConvertToSource(true)
                    .supportsBoating(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                    .canHydrate(true)
                    .lightLevel(10)) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        @Override
                        public ResourceLocation getStillTexture() {
                            return STILL_TEXTURE;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture() {
                            return FLOWING_TEXTURE;
                        }

                        @Override
                        public int getTintColor() {
                            return TINT_COLOR;
                        }
                    });
                }
            });

    public static final RegistryObject<FlowingFluid> BIOLUMINESCENT_WATER = FLUIDS.register(
            "bioluminescent_water", () -> new ForgeFlowingFluid.Source(fluidProperties()));

    public static final RegistryObject<FlowingFluid> BIOLUMINESCENT_WATER_FLOWING = FLUIDS.register(
            "bioluminescent_water_flowing", () -> new ForgeFlowingFluid.Flowing(fluidProperties()));

    private static ForgeFlowingFluid.Properties fluidProperties() {
        return new ForgeFlowingFluid.Properties(
                BIOLUMINESCENT_WATER_TYPE,
                () -> BIOLUMINESCENT_WATER.get(),
                () -> BIOLUMINESCENT_WATER_FLOWING.get())
                .bucket(() -> RunicRealmItems.BIOLUMINESCENT_WATER_BUCKET.get())
                .block(() -> RunicRealmBlocks.BIOLUMINESCENT_WATER_BLOCK.get());
    }

    private RunicRealmFluids() {}
}

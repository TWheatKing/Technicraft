package com.example.technicraft.registry;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import com.example.technicraft.Technicraft;
import com.example.technicraft.block.entity.*;

/**
 * Registry for all Technicraft block entity types
 * Handles registration of tile entities for complex blocks
 */
public class ModBlockEntityTypes {
    // Kinetic system block entities
    public static final BlockEntityType<KineticGeneratorBlockEntity> KINETIC_GENERATOR =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Technicraft.id("kinetic_generator"),
                    BlockEntityType.Builder.create(KineticGeneratorBlockEntity::new, ModBlocks.KINETIC_GENERATOR).build());

    public static final BlockEntityType<BasicMotorBlockEntity> BASIC_MOTOR =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Technicraft.id("basic_motor"),
                    BlockEntityType.Builder.create(BasicMotorBlockEntity::new, ModBlocks.BASIC_MOTOR).build());

    public static final BlockEntityType<ShaftBlockEntity> SHAFT =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Technicraft.id("shaft"),
                    BlockEntityType.Builder.create(ShaftBlockEntity::new, ModBlocks.SHAFT).build());

    // Energy system block entities
    public static final BlockEntityType<EnergyConverterBlockEntity> ENERGY_CONVERTER =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Technicraft.id("energy_converter"),
                    BlockEntityType.Builder.create(EnergyConverterBlockEntity::new, ModBlocks.ENERGY_CONVERTER).build());

    public static final BlockEntityType<BasicBatteryBlockEntity> BASIC_BATTERY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Technicraft.id("basic_battery"),
                    BlockEntityType.Builder.create(BasicBatteryBlockEntity::new, ModBlocks.BASIC_BATTERY).build());

    // Processing machine block entities
    public static final BlockEntityType<MechanicalCrusherBlockEntity> MECHANICAL_CRUSHER =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Technicraft.id("mechanical_crusher"),
                    BlockEntityType.Builder.create(MechanicalCrusherBlockEntity::new, ModBlocks.MECHANICAL_CRUSHER).build());

    public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Technicraft.id("electric_furnace"),
                    BlockEntityType.Builder.create(ElectricFurnaceBlockEntity::new, ModBlocks.ELECTRIC_FURNACE).build());

    /**
     * Initializes all mod block entity types - called during mod initialization
     */
    public static void initialize() {
        Technicraft.LOGGER.info("Registering Technicraft block entity types...");

        // Block entity types are registered via static initialization above
        // This method ensures the class is loaded and types are registered
    }
}
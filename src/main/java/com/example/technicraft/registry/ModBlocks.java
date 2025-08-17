package com.example.technicraft.registry;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

import com.example.technicraft.Technicraft;
import com.example.technicraft.block.*;

/**
 * Registry for all Technicraft blocks
 * Contains machines, components, and infrastructure blocks
 */
public class ModBlocks {
    // Basic kinetic components
    public static final Block SHAFT = registerBlock("shaft",
            new ShaftBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.IRON_GRAY)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .strength(3.0f, 6.0f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()));

    // Kinetic machines
    public static final Block KINETIC_GENERATOR = registerBlock("kinetic_generator",
            new KineticGeneratorBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.COPPER_ORANGE)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .strength(4.0f, 6.0f)
                    .sounds(BlockSoundGroup.METAL)
                    .requiresTool()));

    public static final Block BASIC_MOTOR = registerBlock("basic_motor",
            new BasicMotorBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.IRON_GRAY)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .strength(4.0f, 6.0f)
                    .sounds(BlockSoundGroup.METAL)
                    .requiresTool()));

    // Energy storage and conversion
    public static final Block ENERGY_CONVERTER = registerBlock("energy_converter",
            new EnergyConverterBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.GOLD)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .strength(4.0f, 8.0f)
                    .sounds(BlockSoundGroup.METAL)
                    .requiresTool()));

    public static final Block BASIC_BATTERY = registerBlock("basic_battery",
            new BasicBatteryBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.LIGHT_BLUE)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .strength(3.0f, 6.0f)
                    .sounds(BlockSoundGroup.METAL)
                    .requiresTool()));

    // Processing machines
    public static final Block MECHANICAL_CRUSHER = registerBlock("mechanical_crusher",
            new MechanicalCrusherBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.STONE_GRAY)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(5.0f, 8.0f)
                    .sounds(BlockSoundGroup.STONE)
                    .requiresTool()));

    public static final Block ELECTRIC_FURNACE = registerBlock("electric_furnace",
            new ElectricFurnaceBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.IRON_GRAY)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .strength(4.0f, 6.0f)
                    .sounds(BlockSoundGroup.METAL)
                    .requiresTool()
                    .luminance(state -> state.get(ElectricFurnaceBlock.LIT) ? 13 : 0)));

    /**
     * Registers a block and its corresponding block item
     * @param name The block name/id
     * @param block The block instance to register
     * @return The registered block
     */
    private static Block registerBlock(String name, Block block) {
        // Register the block
        Block registeredBlock = Registry.register(Registries.BLOCK, Technicraft.id(name), block);

        // Register the corresponding block item
        Registry.register(Registries.ITEM, Technicraft.id(name),
                new BlockItem(registeredBlock, new Item.Settings()));

        return registeredBlock;
    }

    /**
     * Initializes all mod blocks - called during mod initialization
     */
    public static void initialize() {
        Technicraft.LOGGER.info("Registering Technicraft blocks...");

        // Blocks are registered via static initialization above
        // This method ensures the class is loaded and blocks are registered
    }
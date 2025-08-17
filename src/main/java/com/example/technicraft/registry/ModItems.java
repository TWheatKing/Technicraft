package com.example.technicraft.registry;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import com.example.technicraft.Technicraft;

/**
 * Registry for all Technicraft items
 * Contains basic components and materials for the mod
 */
public class ModItems {
    // Basic gears for mechanical systems
    public static final Item COPPER_GEAR = registerItem("copper_gear",
            new Item(new Item.Settings()));

    public static final Item IRON_GEAR = registerItem("iron_gear",
            new Item(new Item.Settings()));

    public static final Item STEEL_GEAR = registerItem("steel_gear",
            new Item(new Item.Settings()));

    // Kinetic mechanism - core component for rotational machines
    public static final Item KINETIC_MECHANISM = registerItem("kinetic_mechanism",
            new Item(new Item.Settings()));

    // Electronic components for FE machines
    public static final Item BASIC_CIRCUIT = registerItem("basic_circuit",
            new Item(new Item.Settings()));

    public static final Item ADVANCED_CIRCUIT = registerItem("advanced_circuit",
            new Item(new Item.Settings()));

    // Energy storage components
    public static final Item ENERGY_CELL = registerItem("energy_cell",
            new Item(new Item.Settings()));

    // Raw materials
    public static final Item COPPER_DUST = registerItem("copper_dust",
            new Item(new Item.Settings()));

    public static final Item IRON_DUST = registerItem("iron_dust",
            new Item(new Item.Settings()));

    public static final Item STEEL_INGOT = registerItem("steel_ingot",
            new Item(new Item.Settings()));

    public static final Item STEEL_DUST = registerItem("steel_dust",
            new Item(new Item.Settings()));

    // Tools and utilities
    public static final Item WRENCH = registerItem("wrench",
            new Item(new Item.Settings().maxCount(1)));

    public static final Item MULTIMETER = registerItem("multimeter",
            new Item(new Item.Settings().maxCount(1)));

    /**
     * Registers an item with the given name and properties
     * @param name The item name/id
     * @param item The item instance to register
     * @return The registered item
     */
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Technicraft.id(name), item);
    }

    /**
     * Initializes all mod items - called during mod initialization
     */
    public static void initialize() {
        Technicraft.LOGGER.info("Registering Technicraft items...");

        // Items are registered via static initialization above
        // This method ensures the class is loaded and items are registered
    }
}
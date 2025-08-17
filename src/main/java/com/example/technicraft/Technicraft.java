package com.example.technicraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.technicraft.registry.ModBlocks;
import com.example.technicraft.registry.ModItems;
import com.example.technicraft.registry.ModBlockEntityTypes;
import com.example.technicraft.energy.EnergyNetworking;

/**
 * Main mod initialization class for Technicraft
 * Handles registration of all mod content and systems
 */
public class Technicraft implements ModInitializer {
	// Mod constants
	public static final String MOD_ID = "technicraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Creative tab for our mod items
	public static final RegistryKey<ItemGroup> TECHNICRAFT_TAB = RegistryKey.of(
			Registries.ITEM_GROUP.getKey(),
			Identifier.of(MOD_ID, "technicraft_tab")
	);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Technicraft mod...");

		// Register all mod content in order
		ModItems.initialize();
		ModBlocks.initialize();
		ModBlockEntityTypes.initialize();

		// Initialize energy systems
		EnergyNetworking.initialize();

		// Create creative tab
		createCreativeTab();

		LOGGER.info("Technicraft mod initialization complete!");
	}

	/**
	 * Creates the creative mode tab for our mod items
	 */
	private void createCreativeTab() {
		Registry.register(Registries.ITEM_GROUP, TECHNICRAFT_TAB,
				FabricItemGroup.builder()
						.icon(() -> new ItemStack(ModItems.COPPER_GEAR))
						.displayName(Text.translatable("itemGroup.technicraft.general"))
						.build()
		);

		// Add items to the creative tab
		ItemGroupEvents.modifyEntriesEvent(TECHNICRAFT_TAB).register(entries -> {
			// Add basic components
			entries.add(ModItems.COPPER_GEAR);
			entries.add(ModItems.IRON_GEAR);
			entries.add(ModItems.KINETIC_MECHANISM);

			// Add machine blocks
			entries.add(ModBlocks.KINETIC_GENERATOR);
			entries.add(ModBlocks.BASIC_MOTOR);
			entries.add(ModBlocks.SHAFT);
		});
	}

	/**
	 * Utility method to create identifiers for this mod
	 */
	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
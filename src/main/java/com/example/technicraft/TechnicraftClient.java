package com.example.technicraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

import com.example.technicraft.registry.ModBlocks;

/**
 * Client-side initialization for Technicraft
 * Handles rendering setup and client-only features
 */
public class TechnicraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Technicraft.LOGGER.info("Initializing Technicraft client...");

        // Set up block render layers
        setupRenderLayers();

        Technicraft.LOGGER.info("Technicraft client initialization complete!");
    }

    /**
     * Configure render layers for transparent/translucent blocks
     */
    private void setupRenderLayers() {
        // Set shaft to cutout for better visual appearance
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHAFT, RenderLayer.getCutout());
    }
}
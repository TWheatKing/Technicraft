package com.example.technicraft.energy;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages energy networks and conversion between rotational and FE energy
 * Handles network discovery, energy distribution, and tick updates
 */
public class EnergyNetworking {
    // Conversion rate: 1 SU*RPM = X FE per tick
    public static final float ROTATIONAL_TO_FE_RATIO = 4.0f;

    // Networks by world - maps world to list of energy networks
    private static final Map<World, List<EnergyNetwork>> worldNetworks = new ConcurrentHashMap<>();

    // Dirty networks that need rebuilding
    private static final Set<EnergyNetwork> dirtyNetworks = ConcurrentHashMap.newKeySet();

    /**
     * Initializes the energy networking system
     */
    public static void initialize() {
        // Register server tick event for energy updates
        ServerTickEvents.END_WORLD_TICK.register(EnergyNetworking::onWorldTick);
    }

    /**
     * Called every server tick to update energy networks
     * @param world The world being ticked
     */
    private static void onWorldTick(ServerWorld world) {
        List<EnergyNetwork> networks = worldNetworks.get(world);
        if (networks == null) return;

        // Update all networks in this world
        for (EnergyNetwork network : networks) {
            network.tick();
        }

        // Rebuild dirty networks
        rebuildDirtyNetworks();
    }

    /**
     * Gets or creates an energy network for a position
     * @param world The world
     * @param pos The block position
     * @return The energy network at this position, or null if none exists
     */
    public static EnergyNetwork getNetworkAt(World world, BlockPos pos) {
        List<EnergyNetwork> networks = worldNetworks.computeIfAbsent(world, k -> new ArrayList<>());

        for (EnergyNetwork network : networks) {
            if (network.contains(pos)) {
                return network;
            }
        }

        return null;
    }

    /**
     * Creates a new energy network starting from a position
     * @param world The world
     * @param startPos Starting position for network discovery
     * @return The newly created network
     */
    public static EnergyNetwork createNetwork(World world, BlockPos startPos) {
        EnergyNetwork network = new EnergyNetwork(world);
        network.discoverNetwork(startPos);

        List<EnergyNetwork> networks = worldNetworks.computeIfAbsent(world, k -> new ArrayList<>());
        networks.add(network);

        return network;
    }

    /**
     * Marks a network as dirty and needing rebuild
     * @param network The network to mark as dirty
     */
    public static void markNetworkDirty(EnergyNetwork network) {
        dirtyNetworks.add(network);
    }

    /**
     * Rebuilds all dirty networks
     */
    private static void rebuildDirtyNetworks() {
        if (dirtyNetworks.isEmpty()) return;

        Iterator<EnergyNetwork> iterator = dirtyNetworks.iterator();
        while (iterator.hasNext()) {
            EnergyNetwork network = iterator.next();
            network.rebuild();
            iterator.remove();
        }
    }

    /**
     * Removes a network from tracking
     * @param world The world
     * @param network The network to remove
     */
    public static void removeNetwork(World world, EnergyNetwork network) {
        List<EnergyNetwork> networks = worldNetworks.get(world);
        if (networks != null) {
            networks.remove(network);
        }
        dirtyNetworks.remove(network);
    }

    /**
     * Cleans up networks for a world (called when world unloads)
     * @param world The world to clean up
     */
    public static void cleanupWorld(World world) {
        List<EnergyNetwork> networks = worldNetworks.remove(world);
        if (networks != null) {
            dirtyNetworks.removeAll(networks);
        }
    }

    /**
     * Converts rotational power to FE energy
     * @param rotationalPower Power in SU*RPM
     * @return Energy in FE per tick
     */
    public static long rotationalToFE(float rotationalPower) {
        return Math.round(rotationalPower * ROTATIONAL_TO_FE_RATIO);
    }

    /**
     * Converts FE energy to rotational power
     * @param feEnergy Energy in FE per tick
     * @return Power in SU*RPM
     */
    public static float feToRotational(long feEnergy) {
        return feEnergy / ROTATIONAL_TO_FE_RATIO;
    }

    /**
     * Gets debug information about all networks
     * @param world The world to get info for
     * @return List of debug strings
     */
    public static List<String> getDebugInfo(World world) {
        List<String> info = new ArrayList<>();
        List<EnergyNetwork> networks = worldNetworks.get(world);

        if (networks == null || networks.isEmpty()) {
            info.add("No energy networks in world");
            return info;
        }

        info.add("Energy Networks: " + networks.size());
        for (int i = 0; i < networks.size(); i++) {
            EnergyNetwork network = networks.get(i);
            info.add("Network " + i + ": " + network.getSize() + " blocks");
            info.add("  Generators: " + network.getGeneratorCount());
            info.add("  Consumers: " + network.getConsumerCount());
            info.add("  Total Power: " + network.getTotalPower() + " SU*RPM");
        }

        return info;
    }
}
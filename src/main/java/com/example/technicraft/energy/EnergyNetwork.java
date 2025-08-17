package com.example.technicraft.energy;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import com.example.technicraft.block.entity.IKineticComponent;

import java.util.*;

/**
 * Represents a connected network of kinetic energy components
 * Handles energy distribution and load balancing across the network
 */
public class EnergyNetwork {
    private final World world;
    private final Set<BlockPos> networkBlocks = new HashSet<>();
    private final List<IKineticComponent> generators = new ArrayList<>();
    private final List<IKineticComponent> consumers = new ArrayList<>();
    private final List<IKineticComponent> transmitters = new ArrayList<>();

    // Network statistics
    private float totalPowerGeneration = 0.0f;
    private float totalPowerConsumption = 0.0f;
    private float networkSpeed = 0.0f;
    private float networkStress = 0.0f;

    // Update tracking
    private boolean needsRebuild = false;
    private int ticksSinceLastUpdate = 0;

    public EnergyNetwork(World world) {
        this.world = world;
    }

    /**
     * Discovers and builds the network starting from a position
     * @param startPos Starting position for network discovery
     */
    public void discoverNetwork(BlockPos startPos) {
        networkBlocks.clear();
        generators.clear();
        consumers.clear();
        transmitters.clear();

        // Use BFS to discover all connected kinetic components
        Queue<BlockPos> toExplore = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        toExplore.add(startPos);
        visited.add(startPos);

        while (!toExplore.isEmpty()) {
            BlockPos currentPos = toExplore.poll();
            BlockEntity blockEntity = world.getBlockEntity(currentPos);

            if (blockEntity instanceof IKineticComponent kineticComponent) {
                networkBlocks.add(currentPos);
                categorizeComponent(kineticComponent);

                // Explore connected directions
                Direction[] connectionDirections = kineticComponent.getKineticConnectionDirections();
                for (Direction direction : connectionDirections) {
                    if (kineticComponent.canConnectKinetically(direction)) {
                        BlockPos neighborPos = currentPos.offset(direction);

                        if (!visited.contains(neighborPos)) {
                            BlockEntity neighborEntity = world.getBlockEntity(neighborPos);
                            if (neighborEntity instanceof IKineticComponent neighborKinetic) {
                                // Check if neighbor can connect back to us
                                Direction oppositeDirection = direction.getOpposite();
                                if (neighborKinetic.canConnectKinetically(oppositeDirection)) {
                                    toExplore.add(neighborPos);
                                    visited.add(neighborPos);
                                }
                            }
                        }
                    }
                }
            }
        }

        calculateNetworkStats();
    }

    /**
     * Categorizes a kinetic component into generator, consumer, or transmitter
     * @param component The component to categorize
     */
    private void categorizeComponent(IKineticComponent component) {
        float powerBalance = component.getPowerBalance();

        if (powerBalance > 0) {
            generators.add(component);
        } else if (powerBalance < 0) {
            consumers.add(component);
        } else {
            transmitters.add(component);
        }
    }

    /**
     * Calculates network statistics like total power and stress
     */
    private void calculateNetworkStats() {
        totalPowerGeneration = 0.0f;
        totalPowerConsumption = 0.0f;
        networkStress = 0.0f;

        // Sum up generation
        for (IKineticComponent generator : generators) {
            totalPowerGeneration += generator.getPowerBalance();
        }

        // Sum up consumption (negative values, so we negate them)
        for (IKineticComponent consumer : consumers) {
            totalPowerConsumption += -consumer.getPowerBalance();
        }

        // Sum up stress levels
        for (IKineticComponent component : getAllComponents()) {
            networkStress += component.getStressLevel();
        }

        // Calculate network speed based on power balance
        float netPower = totalPowerGeneration - totalPowerConsumption;
        if (totalPowerGeneration > 0) {
            networkSpeed = Math.max(0, netPower / totalPowerGeneration * getAverageGeneratorSpeed());
        } else {
            networkSpeed = 0;
        }
    }

    /**
     * Gets the average speed of all generators in the network
     * @return Average generator speed
     */
    private float getAverageGeneratorSpeed() {
        if (generators.isEmpty()) return 0;

        float totalSpeed = 0;
        for (IKineticComponent generator : generators) {
            totalSpeed += generator.getRotationalSpeed();
        }

        return totalSpeed / generators.size();
    }

    /**
     * Called every tick to update the network
     */
    public void tick() {
        ticksSinceLastUpdate++;

        // Rebuild network periodically or when flagged
        if (needsRebuild || ticksSinceLastUpdate % 100 == 0) {
            rebuild();
            needsRebuild = false;
            ticksSinceLastUpdate = 0;
        }

        // Update network energy distribution
        updateEnergyDistribution();

        // Update network statistics
        calculateNetworkStats();
    }

    /**
     * Updates energy distribution across the network
     */
    private void updateEnergyDistribution() {
        if (generators.isEmpty()) return;

        // Calculate total available energy from generators
        float totalAvailableEnergy = 0;
        for (IKineticComponent generator : generators) {
            totalAvailableEnergy += generator.provideRotationalEnergy(Float.MAX_VALUE, true);
        }

        if (totalAvailableEnergy <= 0) return;

        // Calculate total energy demand from consumers
        float totalEnergyDemand = 0;
        for (IKineticComponent consumer : consumers) {
            totalEnergyDemand += consumer.receiveRotationalEnergy(Float.MAX_VALUE, true);
        }

        // Distribute energy proportionally
        if (totalEnergyDemand > 0) {
            float distributionRatio = Math.min(1.0f, totalAvailableEnergy / totalEnergyDemand);

            // Extract energy from generators
            float energyPerGenerator = totalEnergyDemand * distributionRatio / generators.size();
            for (IKineticComponent generator : generators) {
                generator.provideRotationalEnergy(energyPerGenerator, false);
            }

            // Distribute energy to consumers
            for (IKineticComponent consumer : consumers) {
                float maxReceive = consumer.receiveRotationalEnergy(Float.MAX_VALUE, true);
                float energyToProvide = maxReceive * distributionRatio;
                consumer.receiveRotationalEnergy(energyToProvide, false);
            }
        }
    }

    /**
     * Rebuilds the network from scratch
     */
    public void rebuild() {
        if (!networkBlocks.isEmpty()) {
            BlockPos startPos = networkBlocks.iterator().next();
            discoverNetwork(startPos);
        }
    }

    /**
     * Checks if a position is part of this network
     * @param pos Position to check
     * @return True if position is in network
     */
    public boolean contains(BlockPos pos) {
        return networkBlocks.contains(pos);
    }

    /**
     * Gets the size of the network
     * @return Number of blocks in network
     */
    public int getSize() {
        return networkBlocks.size();
    }

    /**
     * Gets the number of generators in the network
     * @return Generator count
     */
    public int getGeneratorCount() {
        return generators.size();
    }

    /**
     * Gets the number of consumers in the network
     * @return Consumer count
     */
    public int getConsumerCount() {
        return consumers.size();
    }

    /**
     * Gets the total power generation of the network
     * @return Total power in SU*RPM
     */
    public float getTotalPower() {
        return totalPowerGeneration;
    }

    /**
     * Gets the current network speed
     * @return Network speed in RPM
     */
    public float getNetworkSpeed() {
        return networkSpeed;
    }

    /**
     * Gets the current network stress level
     * @return Network stress in SU
     */
    public float getNetworkStress() {
        return networkStress;
    }

    /**
     * Checks if the network is overstressed
     * @return True if any component is overstressed
     */
    public boolean isOverstressed() {
        for (IKineticComponent component : getAllComponents()) {
            if (component.isOverstressed()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all components in the network
     * @return List of all kinetic components
     */
    private List<IKineticComponent> getAllComponents() {
        List<IKineticComponent> allComponents = new ArrayList<>();
        allComponents.addAll(generators);
        allComponents.addAll(consumers);
        allComponents.addAll(transmitters);
        return allComponents;
    }

    /**
     * Marks the network as needing a rebuild
     */
    public void markDirty() {
        needsRebuild = true;
    }

    /**
     * Gets debug information about this network
     * @return List of debug strings
     */
    public List<String> getDebugInfo() {
        List<String> info = new ArrayList<>();
        info.add("Network Size: " + getSize() + " blocks");
        info.add("Generators: " + getGeneratorCount());
        info.add("Consumers: " + getConsumerCount());
        info.add("Transmitters: " + transmitters.size());
        info.add("Power Generation: " + String.format("%.2f", totalPowerGeneration) + " SU*RPM");
        info.add("Power Consumption: " + String.format("%.2f", totalPowerConsumption) + " SU*RPM");
        info.add("Network Speed: " + String.format("%.2f", networkSpeed) + " RPM");
        info.add("Network Stress: " + String.format("%.2f", networkStress) + " SU");
        info.add("Overstressed: " + isOverstressed());
        return info;
    }
}
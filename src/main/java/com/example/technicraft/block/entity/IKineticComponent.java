package com.example.technicraft.block.entity;

import net.minecraft.util.math.Direction;

/**
 * Interface for block entities that can handle rotational energy
 * Provides standard methods for kinetic energy interaction
 */
public interface IKineticComponent {

    /**
     * Receives rotational energy into this component
     * @param energy Amount of rotational energy to receive (in RPM)
     * @param simulate Whether to simulate the operation without actually receiving
     * @return Amount of energy actually received
     */
    float receiveRotationalEnergy(float energy, boolean simulate);

    /**
     * Provides rotational energy from this component
     * @param maxEnergy Maximum amount of energy to provide
     * @param simulate Whether to simulate the operation without actually providing
     * @return Amount of energy actually provided
     */
    float provideRotationalEnergy(float maxEnergy, boolean simulate);

    /**
     * Gets the current rotational speed of this component
     * @return Current speed in RPM
     */
    float getRotationalSpeed();

    /**
     * Gets the current stress level of this component
     * @return Current stress in SU (Stress Units)
     */
    float getStressLevel();

    /**
     * Gets the maximum stress this component can handle
     * @return Maximum stress in SU
     */
    float getMaxStress();

    /**
     * Checks if this component is currently overstressed
     * @return True if overstressed
     */
    boolean isOverstressed();

    /**
     * Checks if this component can connect kinetically in the given direction
     * @param direction Direction to check
     * @return True if connection is possible
     */
    boolean canConnectKinetically(Direction direction);

    /**
     * Gets all directions this component can connect kinetically
     * @return Array of valid connection directions
     */
    Direction[] getKineticConnectionDirections();

    /**
     * Called when the kinetic network needs to be updated
     */
    default void onKineticNetworkUpdate() {
        // Default implementation - components can override if needed
    }

    /**
     * Gets the power generation/consumption of this component
     * Positive values indicate power generation, negative indicate consumption
     * @return Power in SU*RPM
     */
    default float getPowerBalance() {
        return 0.0f;
    }

    /**
     * Checks if this component is a power source (generator)
     * @return True if this component generates power
     */
    default boolean isPowerSource() {
        return getPowerBalance() > 0;
    }

    /**
     * Checks if this component is a power consumer
     * @return True if this component consumes power
     */
    default boolean isPowerConsumer() {
        return getPowerBalance() < 0;
    }

    /**
     * Gets the efficiency of this component (0.0 to 1.0)
     * Used for energy loss calculations in transmission
     * @return Efficiency ratio
     */
    default float getEfficiency() {
        return 1.0f; // Perfect efficiency by default
    }

    /**
     * Called when this component should apply stress to the network
     * @param stress Amount of stress to apply
     * @return True if stress was successfully applied
     */
    default boolean applyStress(float stress) {
        return true; // Default implementation accepts all stress
    }

    /**
     * Called when stress should be removed from the network
     * @param stress Amount of stress to remove
     */
    default void removeStress(float stress) {
        // Default implementation - components can override
    }
}
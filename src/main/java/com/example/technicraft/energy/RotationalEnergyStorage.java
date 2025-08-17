package com.example.technicraft.energy;

import net.minecraft.nbt.NbtCompound;

/**
 * Handles rotational energy storage and transfer
 * Similar to Create's stress and speed system
 */
public class RotationalEnergyStorage {
    // Current rotational speed in RPM (Rotations Per Minute)
    private float speed = 0.0f;

    // Current stress being used (similar to Create's stress units)
    private float stress = 0.0f;

    // Maximum stress this storage can handle before breaking down
    private float maxStress = 256.0f;

    // Maximum speed this storage can handle
    private float maxSpeed = 256.0f;

    // Whether this storage is overstressed
    private boolean overstressed = false;

    public RotationalEnergyStorage(float maxStress, float maxSpeed) {
        this.maxStress = maxStress;
        this.maxSpeed = maxSpeed;
    }

    /**
     * Sets the rotational speed, clamping to maximum
     * @param speed The new speed in RPM
     */
    public void setSpeed(float speed) {
        this.speed = Math.min(Math.abs(speed), maxSpeed);
        checkOverstressed();
    }

    /**
     * Gets the current rotational speed
     * @return Current speed in RPM
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Adds stress to the current load
     * @param stress Amount of stress to add
     * @return Whether the stress was successfully added
     */
    public boolean addStress(float stress) {
        if (this.stress + stress <= maxStress) {
            this.stress += stress;
            checkOverstressed();
            return true;
        }
        return false;
    }

    /**
     * Removes stress from the current load
     * @param stress Amount of stress to remove
     */
    public void removeStress(float stress) {
        this.stress = Math.max(0, this.stress - stress);
        checkOverstressed();
    }

    /**
     * Gets the current stress level
     * @return Current stress units
     */
    public float getStress() {
        return stress;
    }

    /**
     * Gets the maximum stress capacity
     * @return Maximum stress units
     */
    public float getMaxStress() {
        return maxStress;
    }

    /**
     * Calculates the power output based on speed and available stress
     * Power = Speed * Available Stress
     * @return Power in SU*RPM (Stress Units * RPM)
     */
    public float getPowerOutput() {
        if (overstressed) return 0.0f;
        return speed * (maxStress - stress);
    }

    /**
     * Calculates available stress capacity
     * @return Available stress units
     */
    public float getAvailableStress() {
        return maxStress - stress;
    }

    /**
     * Checks if the system is overstressed and updates status
     */
    private void checkOverstressed() {
        overstressed = stress > maxStress;
    }

    /**
     * Returns whether the system is currently overstressed
     * @return True if overstressed
     */
    public boolean isOverstressed() {
        return overstressed;
    }

    /**
     * Gets the stress utilization as a percentage
     * @return Stress utilization (0.0 to 1.0)
     */
    public float getStressUtilization() {
        return stress / maxStress;
    }

    /**
     * Resets the rotational energy storage
     */
    public void reset() {
        this.speed = 0.0f;
        this.stress = 0.0f;
        this.overstressed = false;
    }

    /**
     * Serializes the rotational energy data to NBT
     * @param nbt The NBT compound to write to
     */
    public void writeToNbt(NbtCompound nbt) {
        nbt.putFloat("speed", speed);
        nbt.putFloat("stress", stress);
        nbt.putFloat("maxStress", maxStress);
        nbt.putFloat("maxSpeed", maxSpeed);
        nbt.putBoolean("overstressed", overstressed);
    }

    /**
     * Deserializes the rotational energy data from NBT
     * @param nbt The NBT compound to read from
     */
    public void readFromNbt(NbtCompound nbt) {
        this.speed = nbt.getFloat("speed");
        this.stress = nbt.getFloat("stress");
        this.maxStress = nbt.getFloat("maxStress");
        this.maxSpeed = nbt.getFloat("maxSpeed");
        this.overstressed = nbt.getBoolean("overstressed");
    }

    /**
     * Creates a copy of this rotational energy storage
     * @return A new RotationalEnergyStorage with the same values
     */
    public RotationalEnergyStorage copy() {
        RotationalEnergyStorage copy = new RotationalEnergyStorage(maxStress, maxSpeed);
        copy.speed = this.speed;
        copy.stress = this.stress;
        copy.overstressed = this.overstressed;
        return copy;
    }
}
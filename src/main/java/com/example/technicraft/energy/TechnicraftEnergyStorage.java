package com.example.technicraft.energy;

import net.minecraft.nbt.NbtCompound;

/**
 * FE (Forge Energy) compatible energy storage for Technicraft
 * Provides RF/FE energy storage with transfer capabilities
 */
public class TechnicraftEnergyStorage {
    // Current energy stored
    protected long energy;

    // Maximum energy capacity
    protected long capacity;

    // Maximum energy that can be received per tick
    protected long maxReceive;

    // Maximum energy that can be extracted per tick
    protected long maxExtract;

    public TechnicraftEnergyStorage(long capacity) {
        this(capacity, capacity, capacity);
    }

    public TechnicraftEnergyStorage(long capacity, long maxTransfer) {
        this(capacity, maxTransfer, maxTransfer);
    }

    public TechnicraftEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = 0;
    }

    /**
     * Receives energy into the storage
     * @param maxReceive Maximum amount to receive
     * @param simulate Whether to simulate the operation
     * @return Amount of energy actually received
     */
    public long receiveEnergy(long maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        long energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

        if (!simulate) {
            energy += energyReceived;
        }

        return energyReceived;
    }

    /**
     * Extracts energy from the storage
     * @param maxExtract Maximum amount to extract
     * @param simulate Whether to simulate the operation
     * @return Amount of energy actually extracted
     */
    public long extractEnergy(long maxExtract, boolean simulate) {
        if (!canExtract()) {
            return 0;
        }

        long energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

        if (!simulate) {
            energy -= energyExtracted;
        }

        return energyExtracted;
    }

    /**
     * Gets the current energy stored
     * @return Current energy amount
     */
    public long getEnergyStored() {
        return energy;
    }

    /**
     * Gets the maximum energy capacity
     * @return Maximum capacity
     */
    public long getMaxEnergyStored() {
        return capacity;
    }

    /**
     * Checks if energy can be received
     * @return True if energy can be received
     */
    public boolean canReceive() {
        return maxReceive > 0;
    }

    /**
     * Checks if energy can be extracted
     * @return True if energy can be extracted
     */
    public boolean canExtract() {
        return maxExtract > 0;
    }

    /**
     * Gets the energy fill percentage
     * @return Fill percentage (0.0 to 1.0)
     */
    public float getEnergyFillPercentage() {
        return capacity > 0 ? (float) energy / capacity : 0.0f;
    }

    /**
     * Checks if the storage is full
     * @return True if at maximum capacity
     */
    public boolean isFull() {
        return energy >= capacity;
    }

    /**
     * Checks if the storage is empty
     * @return True if no energy stored
     */
    public boolean isEmpty() {
        return energy <= 0;
    }

    /**
     * Sets the current energy level directly
     * @param energy New energy level (clamped to capacity)
     */
    public void setEnergy(long energy) {
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    /**
     * Modifies the current energy level
     * @param energy Amount to add (positive) or remove (negative)
     */
    public void modifyEnergyStored(long energy) {
        this.energy = Math.max(0, Math.min(capacity, this.energy + energy));
    }

    /**
     * Sets the maximum receive rate
     * @param maxReceive New maximum receive rate
     */
    public void setMaxReceive(long maxReceive) {
        this.maxReceive = maxReceive;
    }

    /**
     * Sets the maximum extract rate
     * @param maxExtract New maximum extract rate
     */
    public void setMaxExtract(long maxExtract) {
        this.maxExtract = maxExtract;
    }

    /**
     * Gets the maximum receive rate
     * @return Maximum energy that can be received per tick
     */
    public long getMaxReceive() {
        return maxReceive;
    }

    /**
     * Gets the maximum extract rate
     * @return Maximum energy that can be extracted per tick
     */
    public long getMaxExtract() {
        return maxExtract;
    }

    /**
     * Serializes the energy storage to NBT
     * @param nbt NBT compound to write to
     */
    public void writeToNbt(NbtCompound nbt) {
        nbt.putLong("energy", energy);
        nbt.putLong("capacity", capacity);
        nbt.putLong("maxReceive", maxReceive);
        nbt.putLong("maxExtract", maxExtract);
    }

    /**
     * Deserializes the energy storage from NBT
     * @param nbt NBT compound to read from
     */
    public void readFromNbt(NbtCompound nbt) {
        this.energy = nbt.getLong("energy");
        this.capacity = nbt.getLong("capacity");
        this.maxReceive = nbt.getLong("maxReceive");
        this.maxExtract = nbt.getLong("maxExtract");
    }

    /**
     * Creates a copy of this energy storage
     * @return New energy storage with same properties
     */
    public TechnicraftEnergyStorage copy() {
        TechnicraftEnergyStorage copy = new TechnicraftEnergyStorage(capacity, maxReceive, maxExtract);
        copy.energy = this.energy;
        return copy;
    }
}
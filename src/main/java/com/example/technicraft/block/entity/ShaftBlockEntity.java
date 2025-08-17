package com.example.technicraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import com.example.technicraft.block.ShaftBlock;
import com.example.technicraft.energy.EnergyNetwork;
import com.example.technicraft.energy.EnergyNetworking;
import com.example.technicraft.energy.RotationalEnergyStorage;
import com.example.technicraft.registry.ModBlockEntityTypes;

/**
 * Block entity for shaft blocks
 * Handles rotational energy transmission and network connectivity
 */
public class ShaftBlockEntity extends BlockEntity {
    // Rotational energy storage for this shaft
    private final RotationalEnergyStorage rotationalStorage;

    // Reference to the energy network this shaft belongs to
    private EnergyNetwork energyNetwork;

    // Flag to track if this shaft needs network updates
    private boolean networkDirty = true;

    public ShaftBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SHAFT, pos, state);

        // Initialize with moderate capacity - shafts are transmission components
        this.rotationalStorage = new RotationalEnergyStorage(32.0f, 128.0f);
    }

    /**
     * Server tick method - called every server tick
     */
    public static void tick(World world, BlockPos pos, BlockState state, ShaftBlockEntity blockEntity) {
        if (world.isClient) return;

        // Update network if needed
        if (blockEntity.networkDirty) {
            blockEntity.updateNetwork();
            blockEntity.networkDirty = false;
        }

        // Transmit rotational energy to connected shafts
        blockEntity.transmitEnergy();
    }

    /**
     * Updates the energy network for this shaft
     */
    private void updateNetwork() {
        if (world == null || world.isClient) return;

        energyNetwork = EnergyNetworking.getNetworkAt(world, pos);
        if (energyNetwork == null) {
            energyNetwork = EnergyNetworking.createNetwork(world, pos);
        }
    }

    /**
     * Transmits rotational energy to connected components
     */
    private void transmitEnergy() {
        if (world == null || !(world instanceof ServerWorld)) return;

        ShaftBlock shaftBlock = (ShaftBlock) getCachedState().getBlock();
        Direction[] connectionDirections = shaftBlock.getConnectionDirections(world, pos);

        float currentSpeed = rotationalStorage.getSpeed();
        if (currentSpeed <= 0) return;

        // Distribute energy to connected components
        for (Direction direction : connectionDirections) {
            BlockPos targetPos = pos.offset(direction);
            BlockEntity targetEntity = world.getBlockEntity(targetPos);

            if (targetEntity instanceof IKineticComponent kineticComponent) {
                // Transfer a portion of our rotational energy
                float transferAmount = Math.min(currentSpeed * 0.1f, currentSpeed);
                if (kineticComponent.receiveRotationalEnergy(transferAmount, false) > 0) {
                    rotationalStorage.setSpeed(currentSpeed - transferAmount);
                }
            }
        }
    }

    /**
     * Gets the rotational energy storage for this shaft
     * @return The rotational energy storage
     */
    public RotationalEnergyStorage getRotationalStorage() {
        return rotationalStorage;
    }

    /**
     * Sets the rotational speed of this shaft
     * @param speed New speed in RPM
     */
    public void setRotationalSpeed(float speed) {
        rotationalStorage.setSpeed(speed);
        markDirty();
    }

    /**
     * Called when a neighboring block updates
     */
    public void onNeighborUpdate() {
        networkDirty = true;
        markDirty();
    }

    /**
     * Checks if this shaft can connect to a component in the given direction
     * @param direction Direction to check
     * @return True if connection is possible
     */
    public boolean canConnectInDirection(Direction direction) {
        if (world == null) return false;

        ShaftBlock shaftBlock = (ShaftBlock) getCachedState().getBlock();
        return shaftBlock.canConnectInDirection(world, pos, direction);
    }

    /**
     * Gets all connected kinetic components
     * @return Array of connected kinetic components
     */
    public IKineticComponent[] getConnectedComponents() {
        if (world == null) return new IKineticComponent[0];

        ShaftBlock shaftBlock = (ShaftBlock) getCachedState().getBlock();
        Direction[] directions = shaftBlock.getConnectionDirections(world, pos);

        return java.util.Arrays.stream(directions)
                .map(dir -> pos.offset(dir))
                .map(world::getBlockEntity)
                .filter(entity -> entity instanceof IKineticComponent)
                .map(entity -> (IKineticComponent) entity)
                .toArray(IKineticComponent[]::new);
    }

    /**
     * Implements kinetic component interface for network connectivity
     */
    public float receiveRotationalEnergy(float energy, boolean simulate) {
        if (rotationalStorage.isOverstressed()) return 0;

        float currentSpeed = rotationalStorage.getSpeed();
        float newSpeed = Math.min(currentSpeed + energy, rotationalStorage.getMaxEnergyStored());
        float actualReceived = newSpeed - currentSpeed;

        if (!simulate && actualReceived > 0) {
            rotationalStorage.setSpeed(newSpeed);
            markDirty();
        }

        return actualReceived;
    }

    /**
     * Provides rotational energy to the network
     */
    public float provideRotationalEnergy(float maxEnergy, boolean simulate) {
        float currentSpeed = rotationalStorage.getSpeed();
        float provided = Math.min(maxEnergy, currentSpeed);

        if (!simulate && provided > 0) {
            rotationalStorage.setSpeed(currentSpeed - provided);
            markDirty();
        }

        return provided;
    }

    /**
     * Writes data to NBT for persistence
     */
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        NbtCompound energyNbt = new NbtCompound();
        rotationalStorage.writeToNbt(energyNbt);
        nbt.put("rotational_energy", energyNbt);

        nbt.putBoolean("network_dirty", networkDirty);
    }

    /**
     * Reads data from NBT for persistence
     */
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains("rotational_energy")) {
            rotationalStorage.readFromNbt(nbt.getCompound("rotational_energy"));
        }

        networkDirty = nbt.getBoolean("network_dirty");
    }

    /**
     * Called when the block entity is removed from the world
     */
    @Override
    public void markRemoved() {
        super.markRemoved();

        // Notify network of removal
        if (energyNetwork != null) {
            EnergyNetworking.markNetworkDirty(energyNetwork);
        }
    }
}
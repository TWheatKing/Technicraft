package com.example.technicraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import com.example.technicraft.block.KineticGeneratorBlock;
import com.example.technicraft.energy.EnergyNetwork;
import com.example.technicraft.energy.EnergyNetworking;
import com.example.technicraft.energy.RotationalEnergyStorage;
import com.example.technicraft.registry.ModBlockEntityTypes;

/**
 * Block entity for the kinetic generator
 * Generates rotational energy when cranked by players
 */
public class KineticGeneratorBlockEntity extends BlockEntity implements IKineticComponent {
    // Rotational energy storage
    private final RotationalEnergyStorage rotationalStorage;

    // Reference to energy network
    private EnergyNetwork energyNetwork;

    // Ticks remaining for current generation cycle
    private int generationTicks = 0;

    // Energy generated per crank
    private static final float ENERGY_PER_CRANK = 32.0f;

    // Duration of generation after cranking (in ticks)
    private static final int GENERATION_DURATION = 100; // 5 seconds

    // Speed decay rate per tick when not being cranked
    private static final float SPEED_DECAY = 0.95f;

    // Network update flag
    private boolean networkDirty = true;

    public KineticGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.KINETIC_GENERATOR, pos, state);

        // Initialize with high capacity for power generation
        this.rotationalStorage = new RotationalEnergyStorage(128.0f, 256.0f);
    }

    /**
     * Server tick method - handles energy generation and decay
     */
    public static void tick(World world, BlockPos pos, BlockState state, KineticGeneratorBlockEntity blockEntity) {
        if (world.isClient) return;

        // Update network if needed
        if (blockEntity.networkDirty) {
            blockEntity.updateNetwork();
            blockEntity.networkDirty = false;
        }

        // Handle energy generation and decay
        blockEntity.processGeneration();

        // Distribute energy to connected components
        blockEntity.distributeEnergy();
    }

    /**
     * Updates the energy network for this generator
     */
    private void updateNetwork() {
        if (world == null || world.isClient) return;

        energyNetwork = EnergyNetworking.getNetworkAt(world, pos);
        if (energyNetwork == null) {
            energyNetwork = EnergyNetworking.createNetwork(world, pos);
        }
    }

    /**
     * Processes energy generation and natural decay
     */
    private void processGeneration() {
        if (generationTicks > 0) {
            generationTicks--;
            // Maintain speed while being "cranked"
        } else {
            // Apply natural decay when not being cranked
            float currentSpeed = rotationalStorage.getSpeed();
            if (currentSpeed > 0.1f) {
                rotationalStorage.setSpeed(currentSpeed * SPEED_DECAY);
                markDirty();
            }
        }
    }

    /**
     * Distributes generated energy to connected components
     */
    private void distributeEnergy() {
        if (world == null) return;

        float currentSpeed = rotationalStorage.getSpeed();
        if (currentSpeed <= 1.0f) return; // Don't distribute very low speeds

        BlockState state = getCachedState();
        if (!(state.getBlock() instanceof KineticGeneratorBlock generatorBlock)) return;

        Direction outputDirection = generatorBlock.getOutputDirection(state);
        BlockPos outputPos = pos.offset(outputDirection);
        BlockEntity outputEntity = world.getBlockEntity(outputPos);

        if (outputEntity instanceof IKineticComponent kineticComponent) {
            // Try to transfer energy to connected component
            float transferAmount = Math.min(currentSpeed * 0.2f, 10.0f); // Transfer up to 20% or 10 RPM max
            float transferred = kineticComponent.receiveRotationalEnergy(transferAmount, false);

            if (transferred > 0) {
                rotationalStorage.setSpeed(currentSpeed - transferred);
                markDirty();
            }
        }
    }

    /**
     * Called when a player cranks the generator
     * @param player The player doing the cranking
     * @return True if cranking was successful
     */
    public boolean crank(PlayerEntity player) {
        if (rotationalStorage.isOverstressed()) {
            return false;
        }

        // Add energy and reset generation timer
        float currentSpeed = rotationalStorage.getSpeed();
        float newSpeed = Math.min(currentSpeed + ENERGY_PER_CRANK, rotationalStorage.getMaxEnergyStored());

        rotationalStorage.setSpeed(newSpeed);
        generationTicks = GENERATION_DURATION;

        markDirty();
        return true;
    }

    /**
     * Gets the rotational energy storage
     * @return The rotational storage
     */
    public RotationalEnergyStorage getRotationalStorage() {
        return rotationalStorage;
    }

    /**
     * Called when neighboring block updates
     */
    public void onNeighborUpdate() {
        networkDirty = true;
        markDirty();
    }

    // IKineticComponent implementation

    @Override
    public float receiveRotationalEnergy(float energy, boolean simulate) {
        // Generators typically don't receive energy, but we'll allow some for network balancing
        if (rotationalStorage.isOverstressed()) return 0;

        float currentSpeed = rotationalStorage.getSpeed();
        float maxReceive = Math.max(0, rotationalStorage.getMaxEnergyStored() - currentSpeed);
        float actualReceive = Math.min(energy, maxReceive);

        if (!simulate && actualReceive > 0) {
            rotationalStorage.setSpeed(currentSpeed + actualReceive);
            markDirty();
        }

        return actualReceive;
    }

    @Override
    public float provideRotationalEnergy(float maxEnergy, boolean simulate) {
        float currentSpeed = rotationalStorage.getSpeed();
        float provided = Math.min(maxEnergy, currentSpeed * 0.1f); // Provide up to 10% of current speed

        if (!simulate && provided > 0) {
            rotationalStorage.setSpeed(currentSpeed - provided);
            markDirty();
        }

        return provided;
    }

    @Override
    public float getRotationalSpeed() {
        return rotationalStorage.getSpeed();
    }

    @Override
    public float getStressLevel() {
        return rotationalStorage.getStress();
    }

    @Override
    public float getMaxStress() {
        return rotationalStorage.getMaxStress();
    }

    @Override
    public boolean isOverstressed() {
        return rotationalStorage.isOverstressed();
    }

    @Override
    public boolean canConnectKinetically(Direction direction) {
        if (world == null) return false;

        BlockState state = getCachedState();
        if (!(state.getBlock() instanceof KineticGeneratorBlock generatorBlock)) return false;

        // Can only connect in the output direction
        return direction == generatorBlock.getOutputDirection(state);
    }

    @Override
    public Direction[] getKineticConnectionDirections() {
        BlockState state = getCachedState();
        if (!(state.getBlock() instanceof KineticGeneratorBlock generatorBlock)) {
            return new Direction[0];
        }

        return new Direction[]{generatorBlock.getOutputDirection(state)};
    }

    @Override
    public float getPowerBalance() {
        // Positive value indicates this is a power generator
        return generationTicks > 0 ? ENERGY_PER_CRANK / GENERATION_DURATION : 0;
    }

    /**
     * Checks if the generator is currently being cranked
     * @return True if actively generating
     */
    public boolean isGenerating() {
        return generationTicks > 0;
    }

    /**
     * Gets the remaining generation time in ticks
     * @return Ticks remaining
     */
    public int getRemainingGenerationTicks() {
        return generationTicks;
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

        nbt.putInt("generation_ticks", generationTicks);
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

        generationTicks = nbt.getInt("generation_ticks");
        networkDirty = nbt.getBoolean("network_dirty");
    }

    /**
     * Called when the block entity is removed
     */
    @Override
    public void markRemoved() {
        super.markRemoved();

        if (energyNetwork != null) {
            EnergyNetworking.markNetworkDirty(energyNetwork);
        }
    }
}
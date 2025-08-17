package com.example.technicraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import com.example.technicraft.energy.TechnicraftEnergyStorage;
import com.example.technicraft.energy.RotationalEnergyStorage;
import com.example.technicraft.registry.ModBlockEntityTypes;

/**
 * Basic Motor Block Entity - Converts FE to rotational energy
 */
public class BasicMotorBlockEntity extends BlockEntity implements IKineticComponent {
    private final TechnicraftEnergyStorage energyStorage;
    private final RotationalEnergyStorage rotationalStorage;

    public BasicMotorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BASIC_MOTOR, pos, state);
        this.energyStorage = new TechnicraftEnergyStorage(10000, 100); // 10k FE capacity, 100 FE/t
        this.rotationalStorage = new RotationalEnergyStorage(64.0f, 128.0f);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BasicMotorBlockEntity blockEntity) {
        if (world.isClient) return;
        // TODO: Implement FE to rotational conversion
    }

    // IKineticComponent implementation
    @Override
    public float receiveRotationalEnergy(float energy, boolean simulate) {
        return 0; // Motors generate, don't receive
    }

    @Override
    public float provideRotationalEnergy(float maxEnergy, boolean simulate) {
        // TODO: Convert FE to rotational energy
        return 0;
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
        return direction == Direction.UP; // Output upward
    }

    @Override
    public Direction[] getKineticConnectionDirections() {
        return new Direction[]{Direction.UP};
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtCompound energyNbt = new NbtCompound();
        energyStorage.writeToNbt(energyNbt);
        nbt.put("energy", energyNbt);

        NbtCompound rotationalNbt = new NbtCompound();
        rotationalStorage.writeToNbt(rotationalNbt);
        nbt.put("rotational", rotationalNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("energy")) {
            energyStorage.readFromNbt(nbt.getCompound("energy"));
        }
        if (nbt.contains("rotational")) {
            rotationalStorage.readFromNbt(nbt.getCompound("rotational"));
        }
    }
}


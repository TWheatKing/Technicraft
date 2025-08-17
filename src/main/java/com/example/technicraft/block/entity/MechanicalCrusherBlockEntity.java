package com.example.technicraft.block.entity;

import com.example.technicraft.energy.RotationalEnergyStorage;
import com.example.technicraft.registry.ModBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Mechanical Crusher Block Entity - Rotational powered processing
 */
public class MechanicalCrusherBlockEntity extends BlockEntity implements IKineticComponent {
    private final RotationalEnergyStorage rotationalStorage;
    private int processingTicks = 0;

    public MechanicalCrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.MECHANICAL_CRUSHER, pos, state);
        this.rotationalStorage = new RotationalEnergyStorage(32.0f, 64.0f);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MechanicalCrusherBlockEntity blockEntity) {
        if (world.isClient) return;
        // TODO: Implement crushing logic
    }

    // IKineticComponent implementation
    @Override
    public float receiveRotationalEnergy(float energy, boolean simulate) {
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
        return 0; // Crushers consume, don't provide
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
        return direction == Direction.DOWN; // Receive from below
    }

    @Override
    public Direction[] getKineticConnectionDirections() {
        return new Direction[]{Direction.DOWN};
    }

    @Override
    public float getPowerBalance() {
        return -8.0f; // Consumes 8 SU*RPM when active
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtCompound rotationalNbt = new NbtCompound();
        rotationalStorage.writeToNbt(rotationalNbt);
        nbt.put("rotational", rotationalNbt);
        nbt.putInt("processing_ticks", processingTicks);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("rotational")) {
            rotationalStorage.readFromNbt(nbt.getCompound("rotational"));
        }
        processingTicks = nbt.getInt("processing_ticks");
    }
}

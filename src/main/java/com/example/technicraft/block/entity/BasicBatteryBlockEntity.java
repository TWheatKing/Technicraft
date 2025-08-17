package com.example.technicraft.block.entity;

import com.example.technicraft.energy.TechnicraftEnergyStorage;
import com.example.technicraft.registry.ModBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Basic Battery Block Entity - FE energy storage
 */
public class BasicBatteryBlockEntity extends BlockEntity {
    private final TechnicraftEnergyStorage energyStorage;

    public BasicBatteryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BASIC_BATTERY, pos, state);
        this.energyStorage = new TechnicraftEnergyStorage(100000, 1000); // 100k FE capacity
    }

    public static void tick(World world, BlockPos pos, BlockState state, BasicBatteryBlockEntity blockEntity) {
        if (world.isClient) return;
        // TODO: Implement FE energy distribution to neighbors
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtCompound energyNbt = new NbtCompound();
        energyStorage.writeToNbt(energyNbt);
        nbt.put("energy", energyNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("energy")) {
            energyStorage.readFromNbt(nbt.getCompound("energy"));
        }
    }
}

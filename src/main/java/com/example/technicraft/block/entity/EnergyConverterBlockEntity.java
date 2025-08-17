package com.example.technicraft.block.entity;

import com.example.technicraft.energy.RotationalEnergyStorage;
import com.example.technicraft.energy.TechnicraftEnergyStorage;
import com.example.technicraft.registry.ModBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Energy Converter Block Entity - Bidirectional energy conversion
 */
public class EnergyConverterBlockEntity extends BlockEntity {
    private final TechnicraftEnergyStorage energyStorage;
    private final RotationalEnergyStorage rotationalStorage;

    public EnergyConverterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ENERGY_CONVERTER, pos, state);
        this.energyStorage = new TechnicraftEnergyStorage(50000, 500); // 50k FE capacity
        this.rotationalStorage = new RotationalEnergyStorage(128.0f, 256.0f);
    }

    public static void tick(World world, BlockPos pos, BlockState state, EnergyConverterBlockEntity blockEntity) {
        if (world.isClient) return;
        // TODO: Implement bidirectional energy conversion
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

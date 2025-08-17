package com.example.technicraft.block.entity;

import com.example.technicraft.energy.TechnicraftEnergyStorage;
import com.example.technicraft.registry.ModBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Electric Furnace Block Entity - FE powered smelting
 */
public class ElectricFurnaceBlockEntity extends BlockEntity {
    private final TechnicraftEnergyStorage energyStorage;
    private int smeltingTicks = 0;

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ELECTRIC_FURNACE, pos, state);
        this.energyStorage = new TechnicraftEnergyStorage(20000, 200); // 20k FE capacity
    }

    public static void tick(World world, BlockPos pos, BlockState state, ElectricFurnaceBlockEntity blockEntity) {
        if (world.isClient) return;
        // TODO: Implement electric smelting logic
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtCompound energyNbt = new NbtCompound();
        energyStorage.writeToNbt(energyNbt);
        nbt.put("energy", energyNbt);
        nbt.putInt("smelting_ticks", smeltingTicks);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("energy")) {
            energyStorage.readFromNbt(nbt.getCompound("energy"));
        }
        smeltingTicks = nbt.getInt("smelting_ticks");
    }
}

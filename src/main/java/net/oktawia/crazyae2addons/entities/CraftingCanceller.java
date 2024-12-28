package net.oktawia.crazyae2addons.entities;

import appeng.blockentity.AEBaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CraftingCanceller extends AEBaseBlockEntity {
    public CraftingCanceller(BlockEntityType<CraftingCanceller> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }
}

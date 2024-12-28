package net.oktawia.crazyae2addons.entities;

import appeng.api.inventories.InternalInventory;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class CraftingCanceller extends AENetworkedBlockEntity {
    public CraftingCanceller(BlockPos pos, BlockState state) {
        super(RegistryEntities.CRAFTING_CANCELLER_ENTITY.get(), pos, state);
    }
}

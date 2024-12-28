package net.oktawia.crazyae2addons.blocks;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.oktawia.crazyae2addons.menus.CraftingCancellerMenu;
import org.slf4j.Logger;

public class CraftingCancellerBlock extends Block {
    private static final Logger LOGGER = LogUtils.getLogger();

    public CraftingCancellerBlock(Properties props) {
        super(props);
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider(
                (containerId, playerInventory, player) -> new CraftingCancellerMenu(containerId, playerInventory),
                Component.empty().append("dupa")
        );
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(state.getMenuProvider(level, pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}

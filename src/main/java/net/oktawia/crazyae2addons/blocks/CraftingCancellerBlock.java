package net.oktawia.crazyae2addons.blocks;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.menu.locator.MenuLocators;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.oktawia.crazyae2addons.entities.CraftingCanceller;
import net.oktawia.crazyae2addons.menus.RegistryMenus;

import javax.annotation.Nonnull;

public class CraftingCancellerBlock extends AEBaseEntityBlock<CraftingCanceller> implements IUpgradeableObject {

    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    public CraftingCancellerBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(WORKING, false));
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
        var be = getBlockEntity(level, pos);

        if (be != null) {
            if (!level.isClientSide()) {
                openMenu(player, MenuLocators.forBlockEntity(be));
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WORKING);
    }

    public void openMenu(Player player, MenuHostLocator locator) {
        MenuOpener.open(RegistryMenus.CRAFTING_CANCELLER.get(), player, locator);
    }
}

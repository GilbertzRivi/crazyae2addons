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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.oktawia.crazyae2addons.entities.CraftingCanceller;
import net.oktawia.crazyae2addons.menus.RegistryMenus;

public class CraftingCancellerBlock extends AEBaseEntityBlock<CraftingCanceller> implements IUpgradeableObject {

    public CraftingCancellerBlock(Properties props) {
        super(props);
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

    public void openMenu(Player player, MenuHostLocator locator) {
        MenuOpener.open(RegistryMenus.CRAFTING_CANCELLER.get(), player, locator);
    }
}

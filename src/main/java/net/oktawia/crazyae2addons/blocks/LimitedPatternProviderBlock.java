package net.oktawia.crazyae2addons.blocks;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.block.AEBaseEntityBlock;
import appeng.block.crafting.PatternProviderBlock;
import appeng.block.crafting.PushDirection;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import appeng.util.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.oktawia.crazyae2addons.entities.LimitedPatternProvider;
import static appeng.block.crafting.PatternProviderBlock.PUSH_DIRECTION;

public class LimitedPatternProviderBlock extends AEBaseEntityBlock<LimitedPatternProvider> implements IUpgradeableObject {

    public LimitedPatternProviderBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(PatternProviderBlock.PUSH_DIRECTION, PushDirection.ALL)
        );
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PUSH_DIRECTION);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void neighborChanged(
            BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        var be = getBlockEntity(level, pos);

        if (be != null) {
            be.getLogic().updateRedstoneState();
        }
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
        if (InteractionUtil.isInAlternateUseMode(player)) {
            return InteractionResult.PASS;
        }

        var be = getBlockEntity(level, pos);

        if (be != null) {
            if (!level.isClientSide()) {
                be.openMenu(player, MenuLocators.forBlockEntity(be));
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack heldItem,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit) {
        if (InteractionUtil.canWrenchRotate(heldItem)) {
            setSide(level, pos, hit.getDirection());
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        return super.useItemOn(heldItem, state, level, pos, player, hand, hit);
    }

    public void setSide(Level level, BlockPos pos, Direction facing) {
        var currentState = level.getBlockState(pos);
        var pushSide = currentState.getValue(PUSH_DIRECTION).getDirection();

        PushDirection newPushDirection;
        if (pushSide == facing.getOpposite()) {
            newPushDirection = PushDirection.fromDirection(facing);
        } else if (pushSide == facing) {
            newPushDirection = PushDirection.ALL;
        } else if (pushSide == null) {
            newPushDirection = PushDirection.fromDirection(facing.getOpposite());
        } else {
            newPushDirection = PushDirection.fromDirection(Platform.rotateAround(pushSide, facing));
        }

        level.setBlockAndUpdate(pos, currentState.setValue(PUSH_DIRECTION, newPushDirection));
    }
}
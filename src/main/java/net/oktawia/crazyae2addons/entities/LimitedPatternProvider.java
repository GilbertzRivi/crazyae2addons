package net.oktawia.crazyae2addons.entities;

import appeng.api.ids.AEComponents;
import appeng.api.networking.IGridNodeListener;
import appeng.api.orientation.BlockOrientation;
import appeng.api.stacks.AEItemKey;
import appeng.api.util.AECableType;
import appeng.block.crafting.PatternProviderBlock;
import appeng.block.crafting.PushDirection;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.util.Platform;
import appeng.util.SettingsFrom;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.crazyae2addons.blocks.LimitedPatternProviderBlock;
import net.oktawia.crazyae2addons.logic.LimitedPatternProviderLogic;
import net.oktawia.crazyae2addons.logic.LimitedPatternProviderLogicHost;
import net.oktawia.crazyae2addons.registries.RegistryBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class LimitedPatternProvider extends AENetworkedBlockEntity implements LimitedPatternProviderLogicHost {
    protected final LimitedPatternProviderLogic logic;

    public LimitedPatternProvider(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.logic = createLogic();
    }

    protected LimitedPatternProvider(
            BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState, int slots) {
        super(blockEntityType, pos, blockState);

        this.logic = createLogic(slots);
    }

    protected LimitedPatternProviderLogic createLogic() {
        return createLogic(1);
    }

    protected LimitedPatternProviderLogic createLogic(int slots) {
        return new LimitedPatternProviderLogic(this.getMainNode(), this, slots);
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        this.logic.onMainNodeStateChanged();
        this.updateState();
    }

    public void updateState() {
        if (!this.getMainNode().isReady()) {
            return;
        }

        var connected = false;
        var grid = getMainNode().getGrid();
        if (grid != null) {
            if (grid.getEnergyService().isNetworkPowered()) {
                connected = true;
            }
        }

        if (this.checkPosition(this.worldPosition)) {
            this.level.setBlock(
                    this.worldPosition,
                    this.level
                            .getBlockState(this.worldPosition),
                    Block.UPDATE_CLIENTS);
        }
    }

    private boolean checkPosition(BlockPos pos) {
        return Platform.getTickingBlockEntity(getLevel(), pos) instanceof LimitedPatternProvider;
    }

    private PushDirection getPushDirection() {
        return getBlockState().getValue(PatternProviderBlock.PUSH_DIRECTION);
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        // In omnidirectional mode, every side is grid-connectable
        var pushDirection = getPushDirection().getDirection();
        if (pushDirection == null) {
            return EnumSet.allOf(Direction.class);
        }

        // Otherwise all sides *except* the target side are connectable
        return EnumSet.complementOf(EnumSet.of(pushDirection));
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        this.logic.addDrops(drops);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.logic.clearContent();
    }

    @Override
    public void onReady() {
        super.onReady();
        this.logic.updatePatterns();

        this.updateState();
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.logic.writeToNBT(data, registries);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.logic.readFromNBT(data, registries);
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }

    @Override
    public LimitedPatternProviderLogic getLogic() {
        return logic;
    }

    @Override
    public EnumSet<Direction> getTargets() {
        var pushDirection = getPushDirection();
        if (pushDirection.getDirection() == null) {
            return EnumSet.allOf(Direction.class);
        } else {
            return EnumSet.of(pushDirection.getDirection());
        }
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(RegistryBlocks.LIMITED_PATTERN_PROVIDER);
    }

    @Override
    public void exportSettings(SettingsFrom mode, DataComponentMap.Builder builder, @Nullable Player player) {
        super.exportSettings(mode, builder, player);

        if (mode == SettingsFrom.MEMORY_CARD) {
            logic.exportSettings(builder);

            var pushDirection = getPushDirection();
            builder.set(AEComponents.EXPORTED_PUSH_DIRECTION, pushDirection);
        }
    }

    @Override
    public void importSettings(SettingsFrom mode, DataComponentMap input, @Nullable Player player) {
        super.importSettings(mode, input, player);

        if (mode == SettingsFrom.MEMORY_CARD) {
            logic.importSettings(input, player);

            var pushDirection = input.get(AEComponents.EXPORTED_PUSH_DIRECTION);
            if (pushDirection != null) {
                var level = getLevel();
                if (level != null) {
                    level.setBlockAndUpdate(
                            getBlockPos(),
                            getBlockState().setValue(PatternProviderBlock.PUSH_DIRECTION, pushDirection));
                }
            }
        }
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(RegistryBlocks.LIMITED_PATTERN_PROVIDER);
    }

    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        onGridConnectableSidesChanged();
    }
}

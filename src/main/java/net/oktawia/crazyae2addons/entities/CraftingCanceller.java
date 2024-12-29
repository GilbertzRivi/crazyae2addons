package net.oktawia.crazyae2addons.entities;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.core.settings.TickRates;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.crazyae2addons.menus.CraftingCancellerMenu;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class CraftingCanceller extends AENetworkedBlockEntity implements MenuProvider, IUpgradeableObject, IGridTickable {
    private boolean enabled;
    private int duration;
    private static final Logger LOGGER = LogUtils.getLogger();

    public CraftingCanceller(BlockEntityType<CraftingCanceller> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
        duration = 0;
        enabled = false;
        this.getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .setIdlePowerUsage(0)
                .addService(IGridTickable.class,this);
    }

    public void setEnabled(boolean state){
        enabled = state;
    }

    public void setDuration(int newDuration){
        duration = newDuration;
    }

    public boolean getEnabled(){
        return enabled;
    }

    public int getDuration(){
        return duration;
    }

    @Override
    public boolean hasCustomName() {
        return super.hasCustomName();
    }

    @Override
    public Component getDisplayName() {
        return super.getDisplayName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CraftingCancellerMenu(containerId, playerInventory, this);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(TickRates.Interface, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        LOGGER.info(String.format("%b | %d", this.enabled, this.duration));
        return TickRateModulation.IDLE;
    }
}

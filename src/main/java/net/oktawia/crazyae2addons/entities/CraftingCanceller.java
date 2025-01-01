package net.oktawia.crazyae2addons.entities;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.crafting.*;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.AEKey;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.me.helpers.MachineSource;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import appeng.api.networking.crafting.ICraftingSimulationRequester;
import net.oktawia.crazyae2addons.menus.CraftingCancellerMenu;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import net.oktawia.crazyae2addons.Utils;

public class CraftingCanceller extends AENetworkedBlockEntity implements MenuProvider, IUpgradeableObject, IGridTickable {
    private boolean enabled;
    private int duration;
    private List<ICraftingCPU> craftingCpus;
    private Instant intervalStart;
    private static final Logger LOGGER = LogUtils.getLogger();

    public CraftingCanceller(BlockEntityType<CraftingCanceller> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
        duration = 0;
        enabled = false;
        this.getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .setIdlePowerUsage(4)
                .addService(IGridTickable.class,this);
    }

    @Override
    public void onReady() {
        super.onReady();
        this.intervalStart = Instant.now();
        this.craftingCpus = List.copyOf(getCraftingCpus());
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
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        this.markForUpdate();
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

    public boolean isSleeping(){
        return false;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(1, 5, isSleeping());
    }

    public List<ICraftingCPU> getCraftingCpus(){
        return this.getMainNode().getGrid().getCraftingService().getCpus().stream().toList();
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode iGridNode, int ticksSinceLastCall) {
        if(Instant.now().getEpochSecond() - intervalStart.getEpochSecond() > duration &&
                enabled &&
                duration != 0
        ){
            List<ICraftingCPU> newCpus = null;
            if (Instant.now().getEpochSecond() - intervalStart.getEpochSecond() > duration * 2){
                intervalStart = Instant.now();
            }
            else{
                newCpus = getCraftingCpus();
                List<ICraftingCPU> matchedCpus = newCpus.stream()
                    .filter(cpu -> cpu.getJobStatus() != null)
                    .filter(cpu -> getCraftingCpus().stream()
                        .map(ICraftingCPU::getJobStatus)
                        .filter(Objects::nonNull)
                        .anyMatch(job -> cpu.getJobStatus().progress() == job.progress()
                            && cpu.getJobStatus().crafting().equals(job.crafting())))
                    .toList();

                matchedCpus.forEach(x -> {
                    AEKey item = Objects.requireNonNull(x.getJobStatus()).crafting().what();
                    long amount = Objects.requireNonNull(x.getJobStatus()).crafting().amount();
                    x.cancelJob();
                    ICraftingSimulationRequester simRequester = () -> new MachineSource(getGridNode().getGrid()::getPivot);
                    ICraftingService craftingService = getMainNode().getGrid().getService(ICraftingService.class);
                    Future<ICraftingPlan> futurePlan = craftingService.beginCraftingCalculation(
                        getMainNode().getNode().getLevel(),
                        simRequester,
                        item,
                        amount,
                        CalculationStrategy.REPORT_MISSING_ITEMS);

                    Utils.asyncDelay(
                        () -> CompletableFuture.runAsync(() -> {
                            try {
                                ICraftingPlan craftingPlan = futurePlan.get();
                                getMainNode().getGrid().getCraftingService().submitJob(
                                    craftingPlan,
                                    null,
                                    null,
                                    true,
                                    simRequester.getActionSource());
                            } catch (Exception ignored) {
                            }
                        }), 5);
                });
            }
            intervalStart = Instant.now();
            if (!Objects.isNull((newCpus))){
                this.craftingCpus = List.copyOf(newCpus);
            }
        }
        return TickRateModulation.IDLE;
    }
}

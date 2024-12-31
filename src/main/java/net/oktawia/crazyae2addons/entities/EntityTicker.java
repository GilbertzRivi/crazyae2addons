package net.oktawia.crazyae2addons.entities;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.*;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.stacks.AEKey;
import appeng.me.helpers.MachineSource;
import appeng.parts.AEBasePart;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.oktawia.crazyae2addons.Utils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class EntityTicker extends AEBasePart implements IGridTickable{
    private static final Logger LOGGER = LogUtils.getLogger();
    public EntityTicker(IPartItem<?> partItem) {
        super(partItem);
        this.getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .setIdlePowerUsage(1024)
                .addService(IGridTickable.class,this);
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(5, 5, 12, 11, 11, 13);
        bch.addBox(3, 3, 13, 13, 13, 14);
        bch.addBox(2, 2, 14, 14, 14, 16);
    }

    private boolean isSleeping() {
        return false;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(1, 20, isSleeping());
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode iGridNode, int ticksSinceLastCall) {
        BlockEntity target = getLevel().getBlockEntity(getBlockEntity().getBlockPos().relative(getSide()));
        if (target != null){
            tickBlockEntity(target);
        }
        LOGGER.info("cjik");
        return TickRateModulation.URGENT;
    }

    private  <T extends BlockEntity> void tickBlockEntity(@NotNull T blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        BlockEntityTicker<T> blockEntityTicker = this.getLevel().getBlockState(pos).getTicker(this.getLevel(),
                (BlockEntityType<T>) blockEntity.getType());
        if (blockEntityTicker == null) return;
        for (int i = 0; i < 16 - 1; i++) {
            blockEntityTicker.tick(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(),
                    blockEntity);
        }
    }

}
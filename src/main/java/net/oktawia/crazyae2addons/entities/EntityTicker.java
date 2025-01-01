package net.oktawia.crazyae2addons.entities;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.Settings;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.core.definitions.AEItems;
import appeng.items.parts.PartModels;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.automation.UpgradeablePart;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.menus.EntityTickerMenu;
import net.oktawia.crazyae2addons.registries.RegistryMenus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import java.util.List;
import net.oktawia.crazyae2addons.helpers.CablePartModels;

import static java.lang.Math.pow;

public class EntityTicker extends UpgradeablePart implements IGridTickable,
        MenuProvider, IUpgradeableObject {

    public static final float energyUsageScaleValue = 1f;
    public static final int actionsPerTick = 2;
    private static final CablePartModels MODELS = new CablePartModels(CrazyAddons.makeId("part/entity_ticker"));
    public EntityTickerMenu menu;

    @PartModels
    public static List<IPartModel> getModels() {
        return MODELS.getModels();
    }

    private static final Logger LOGGER = LogUtils.getLogger();
    public EntityTicker(IPartItem<?> partItem) {
        super(partItem);
        this.getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .setIdlePowerUsage(1)
                .addService(IGridTickable.class,this);
    }

    public IPartModel getStaticModels() {
        return MODELS.getModel(this.isPowered(), this.isActive());
    }

    @Override
    public boolean onUseWithoutItem(Player p, Vec3 pos) {
        if (!p.getCommandSenderWorld().isClientSide()) {
            MenuOpener.open(RegistryMenus.ENTITY_TICKER.get(), p, MenuLocators.forPart(this));
        }
        return true;
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(5, 5, 12, 11, 11, 13);
        bch.addBox(3, 3, 13, 13, 13, 14);
        bch.addBox(2, 2, 14, 14, 14, 16);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(20/actionsPerTick, 20/actionsPerTick, false);
    }

    @Override
    public void upgradesChanged() {
        menu.sendUpgradeNum(this.getUpgrades().getInstalledUpgrades(AEItems.SPEED_CARD));
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode iGridNode, int ticksSinceLastCall) {
        BlockEntity target = getLevel().getBlockEntity(getBlockEntity().getBlockPos().relative(getSide()));
        if (target != null && isActive()){
            tickBlockEntity(target);
        }
        return TickRateModulation.IDLE;
    }

    private  <T extends BlockEntity> void tickBlockEntity(@NotNull T blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        BlockEntityTicker<T> blockEntityTicker = this.getLevel().getBlockState(pos).getTicker(this.getLevel(),
                (BlockEntityType<T>) blockEntity.getType());
        if (blockEntityTicker == null) return;
        int speed = (int) pow(2, getUpgrades().getInstalledUpgrades(AEItems.SPEED_CARD));
        int powerDraw = (int) ((1024 * pow(4, energyUsageScaleValue * getUpgrades().getInstalledUpgrades(AEItems.SPEED_CARD))) / actionsPerTick);
        getMainNode().getGrid().getEnergyService().extractAEPower(powerDraw, Actionable.MODULATE, PowerMultiplier.CONFIG);
        for (int i = 0; i < speed * (8 / actionsPerTick) - 1; i++) {
            blockEntityTicker.tick(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(),
                    blockEntity);
        }
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
        return new EntityTickerMenu(containerId, playerInventory, this);
    }

    @Override
    protected int getUpgradeSlots() {
        return 8;
    }
}
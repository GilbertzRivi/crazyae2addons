package net.oktawia.crazyae2addons.entities;

import appeng.api.config.Settings;
import appeng.api.inventories.BaseInternalInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartItem;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.ISubMenuHost;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.menu.locator.MenuLocators;
import appeng.parts.automation.ExportBusPart;
import appeng.util.ConfigInventory;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.prioritylist.DefaultPriorityList;
import appeng.util.prioritylist.IPartitionList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.oktawia.crazyae2addons.helpers.ComponentsUtils;
import net.oktawia.crazyae2addons.implementations.StackTransferContextImplementation;
import net.oktawia.crazyae2addons.menus.NBTExportBusMenu;
import net.oktawia.crazyae2addons.registries.RegistryMenus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import java.util.HashMap;
import java.util.stream.Collectors;
import net.oktawia.crazyae2addons.CrazyAddons;

public class NBTExportBus extends ExportBusPart {
    private static final Logger LOGGER = LogUtils.getLogger();
    public final AppEngInternalInventory inventory = new AppEngInternalInventory(this.internalInventoryHost, 1);
    public NonNullList<TypedDataComponent<?>> components = NonNullList.create();
    public ConfigInventory config;
    public boolean matchMode = true;

    public NBTExportBus(IPartItem<?> partItem) {
        super(partItem);
        this.config = ConfigInventory.configTypes(1).supportedTypes(AEKeyType.items())
                .changeListener(() -> {}).build();
    }

    public final InternalInventoryHost internalInventoryHost = new InternalInventoryHost() {
        @Override
        public void saveChangedInventory(AppEngInternalInventory inv) {
            LOGGER.info(inv.toString());
        }

        @Override
        public boolean isClientSide() {
            return false;
        }
    };

    public NonNullList<AEItemKey> getItemKeys(KeyCounter keyCounter){
        NonNullList<AEItemKey> keys = NonNullList.create();
        keyCounter.forEach(
                key -> {
                if (key.getKey() instanceof AEItemKey){
                    keys.add((AEItemKey) key.getKey());
                }
            }
        );
        return keys;
    }

    public NonNullList<Item> getItems(NonNullList<AEItemKey> keys){
        NonNullList<Item> items = NonNullList.create();
        keys.forEach(
            key -> items.add(getItem(key))
        );
        return items;
    }

    public Item getItem(AEItemKey key){
        return key.getItem();
    }

    public NonNullList<TypedDataComponent<?>> getComponents(Item itemInstance){
        NonNullList<TypedDataComponent<?>> components = NonNullList.create();
        itemInstance.components().forEach(
                components::add
        );
        return components;
    }

    public NonNullList<AEItemKey> filterKeysByComponent(NonNullList<AEItemKey> keys, TypedDataComponent<?> component){
        return keys.stream().filter(
            key -> getComponents(getItem(key)).stream().map(
                    TypedDataComponent::type).toList().contains(component.type())
        ).collect(Collectors.toCollection(NonNullList::create));
    }

    @Override
    protected boolean doBusWork(IGrid grid) {
        var storageService = grid.getStorageService();
        var schedulingMode = this.getConfigManager().getSetting(Settings.SCHEDULING_MODE);
        var context = createTransferContext(storageService, grid.getEnergyService());
        var stacks = grid.getStorageService().getInventory().getAvailableStacks();
//        var cmp = getComponents(getItem(getItemKeys(stacks).getFirst())).getLast();
//        var ks = getItemKeys(stacks);
//        var foo = filterKeysByComponent(ks, cmp);
//
//        int x;
//        for (x = 0; x < this.availableSlots() && context.hasOperationsLeft(); x++) {
//            final int slotToExport = this.getStartingSlot(schedulingMode, x);
//            var what = getConfig().getKey(slotToExport);
//
//            if (what == null) {
//                continue;
//            }
//
//            var transferFactor = what.getAmountPerOperation();
//            long amount = (long) context.getOperationsRemaining() * transferFactor;
//            amount = getExportStrategy().transfer(context, what, amount);
//            if (amount > 0) {
//                context.reduceOperationsRemaining(Math.max(1, amount / transferFactor));
//            }
//        }
//
//        if (context.hasDoneWork()) {
//            this.updateSchedulingMode(schedulingMode, x);
//        }
//
//        return context.hasDoneWork();
        return true;
    }

    private StackTransferContextImplementation createTransferContext(IStorageService storageService, IEnergyService energyService) {
        return new StackTransferContextImplementation(
                storageService,
                energyService,
                this.source,
                getOperationsPerTick(),
                DefaultPriorityList.INSTANCE) {
        };
    }

    @Override
    protected MenuType<?> getMenuType() {
        return RegistryMenus.NBT_EXPORT_BUS.get();
    }
}

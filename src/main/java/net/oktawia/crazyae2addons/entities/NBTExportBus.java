package net.oktawia.crazyae2addons.entities;

import appeng.api.config.Settings;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPartItem;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.me.service.StorageService;
import appeng.parts.automation.ExportBusPart;
import appeng.util.prioritylist.DefaultPriorityList;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.Item;
import net.oktawia.crazyae2addons.implementations.StackTransferContextImplementation;
import org.slf4j.Logger;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class NBTExportBus extends ExportBusPart {
    private static final Logger LOGGER = LogUtils.getLogger();

    public NBTExportBus(IPartItem<?> partItem) {
        super(partItem);
    }

    public ArrayList<AEItemKey> getItemKeys(IGrid grid){
        ArrayList<AEItemKey> keys = new ArrayList<>();
        grid.getStorageService().getInventory().getAvailableStacks().forEach(
                key -> {
                if (key.getKey() instanceof AEItemKey){
                    keys.add((AEItemKey) key.getKey());
                }
            }
        );
        return keys;
    }

    public ArrayList<Item> getItems(ArrayList<AEItemKey> keys){
        ArrayList<Item> items = new ArrayList<>();
        keys.forEach(
            key -> items.add(getItem(key))
        );
        return items;
    }

    public Item getItem(AEItemKey key){
        return key.getItem();
    }

    public ArrayList<TypedDataComponent<?>> getComponents(Item itemInstance){
        ArrayList<TypedDataComponent<?>> components = new ArrayList<>();
        itemInstance.components().forEach(
            c -> components.add(c)
        );
        return components;
    }

    public ArrayList<AEItemKey> filterKeysByComponent(ArrayList<AEItemKey> keys, TypedDataComponent<?> component){
        return keys.stream().filter(
            key -> {
                return getComponents(getItem(key)).stream().map(
                        x -> x.type()).toList().contains(component.type());
            }
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    protected boolean doBusWork(IGrid grid) {
        var storageService = grid.getStorageService();
        var schedulingMode = this.getConfigManager().getSetting(Settings.SCHEDULING_MODE);
        var context = createTransferContext(storageService, grid.getEnergyService());
        var cmp = getComponents(getItem(getItemKeys(grid).getFirst())).getLast();
        var ks = getItemKeys(grid);
        var foo = filterKeysByComponent(ks, cmp);
        int x;
        for (x = 0; x < this.availableSlots() && context.hasOperationsLeft(); x++) {
            final int slotToExport = this.getStartingSlot(schedulingMode, x);
            var what = getConfig().getKey(slotToExport);

            if (what == null) {
                continue;
            }

            var transferFactor = what.getAmountPerOperation();
            long amount = (long) context.getOperationsRemaining() * transferFactor;
            amount = getExportStrategy().transfer(context, what, amount);
            if (amount > 0) {
                context.reduceOperationsRemaining(Math.max(1, amount / transferFactor));
            }
        }

        if (context.hasDoneWork()) {
            this.updateSchedulingMode(schedulingMode, x);
        }

        return context.hasDoneWork();
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
}

package net.oktawia.crazyae2addons.items;

import appeng.items.parts.PartItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import static net.oktawia.crazyae2addons.CrazyAddons.MODID;
import net.oktawia.crazyae2addons.entities.RRItemP2PTunnel;

public class RegistryItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static DeferredItem<PartItem<RRItemP2PTunnel>> RRItemP2PTunnel = ITEMS.register(
            "rr_item_p2p_tunnel",
            () -> new PartItem<>(new Item.Properties(), RRItemP2PTunnel.class, RRItemP2PTunnel::new));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

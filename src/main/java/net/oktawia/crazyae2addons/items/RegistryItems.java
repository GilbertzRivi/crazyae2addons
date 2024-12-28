package net.oktawia.crazyae2addons.items;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import static net.oktawia.crazyae2addons.CrazyAddons.MODID;
import net.oktawia.crazyae2addons.entities.RRItemP2PTunnel;

import java.util.function.Function;

public class RegistryItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final ItemDefinition<PartItem<RRItemP2PTunnel>> RR_ITEM_P2P_TUNNEL = part(
            "RR Item P2P Tunnel", "rr_item_p2p_tunnel", RRItemP2PTunnel.class, RRItemP2PTunnel::new);

    private static <T extends IPart> ItemDefinition<PartItem<T>> part(
            String englishName, String id, Class<T> partClass, Function<IPartItem<T>, T> factory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return item(englishName, id, p -> new PartItem<>(p, partClass, factory));
    }

    private static <T extends Item> ItemDefinition<T> item(
            String englishName, String id, Function<Item.Properties, T> factory) {
        var definition = new ItemDefinition<>(englishName, ITEMS.registerItem(id, factory));
        return definition;
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

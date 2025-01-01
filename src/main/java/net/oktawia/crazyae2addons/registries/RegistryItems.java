package net.oktawia.crazyae2addons.registries;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.core.definitions.*;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import static net.oktawia.crazyae2addons.CrazyAddons.MODID;

import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.entities.EntityTicker;
import net.oktawia.crazyae2addons.entities.NBTExportBus;
import net.oktawia.crazyae2addons.entities.RRItemP2PTunnel;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RegistryItems {
    public static ArrayList<ItemDefinition<?>> ITEM_DEFS = new ArrayList<>();
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final ItemDefinition<PartItem<RRItemP2PTunnel>> RR_ITEM_P2P_TUNNEL = part(
            "RR Item P2P Tunnel", "rr_item_p2p_tunnel", RRItemP2PTunnel.class, RRItemP2PTunnel::new);

    public static final ItemDefinition<PartItem<EntityTicker>> ENTITY_TICKER = part(
            "Entity Ticker", "entity_ticker", EntityTicker.class, EntityTicker::new);

    public static final ItemDefinition<PartItem<NBTExportBus>> NBT_EXPORT_BUS = part(
            "NBT Export Bus", "nbt_export_bus", NBTExportBus.class, NBTExportBus::new);

    private static <T extends IPart> ItemDefinition<PartItem<T>> part(
            String englishName, String id, Class<T> partClass, Function<IPartItem<T>, T> factory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return item(englishName, id, p -> new PartItem<>(p, partClass, factory));
    }

    public static <T extends Item> ItemDefinition<T> item(
            String englishName, String id, Function<Item.Properties, T> factory) {
        var def = new ItemDefinition<>(englishName, ITEMS.registerItem(id, factory));
        ITEM_DEFS.add(def);
        return def;
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

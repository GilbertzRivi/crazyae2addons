package net.oktawia.crazyae2addons.items;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.blockentity.misc.InscriberBlockEntity;
import appeng.core.definitions.*;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import static net.oktawia.crazyae2addons.CrazyAddons.MODID;

import net.oktawia.crazyae2addons.blocks.RegistryBlocks;
import net.oktawia.crazyae2addons.entities.RRItemP2PTunnel;
import net.oktawia.crazyae2addons.entities.CraftingCanceller;
import net.oktawia.crazyae2addons.entities.RegistryEntities;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

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

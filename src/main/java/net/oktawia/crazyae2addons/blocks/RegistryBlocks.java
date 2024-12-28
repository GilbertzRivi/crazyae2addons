package net.oktawia.crazyae2addons.blocks;

import appeng.block.AEBaseEntityBlock;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.items.RegistryItems;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import appeng.block.AEBaseBlockItem;

import javax.annotation.Nullable;

public class RegistryBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CrazyAddons.MODID);

    public static final BlockDefinition<CraftingCancellerBlock> CRAFTING_CANCELLER = block(
            "Crafting Canceller",
            "crafting_canceller",
            () -> new CraftingCancellerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)),
            AEBaseBlockItem::new
    );
    private static <T extends Block> BlockDefinition<T> block(
            String englishName,
            String id,
            Supplier<T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        var block = BLOCKS.register(id, blockSupplier);
        var item = RegistryItems.ITEMS.register(id, () -> itemFactory.apply(block.get(), new Item.Properties()));

        var definition = new BlockDefinition<>(englishName, block, new ItemDefinition<>(englishName, item));
        return definition;
    }
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

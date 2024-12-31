package net.oktawia.crazyae2addons.registries;

import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.blocks.CraftingCancellerBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import appeng.block.AEBaseBlockItem;
import net.oktawia.crazyae2addons.blocks.LimitedPatternProviderBlock;

import javax.annotation.Nullable;

public class RegistryBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CrazyAddons.MODID);

    public static final BlockDefinition<CraftingCancellerBlock> CRAFTING_CANCELLER = block(
            "Crafting Canceller",
            "crafting_canceller",
            () -> new CraftingCancellerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)),
            AEBaseBlockItem::new
    );

    public static final BlockDefinition<LimitedPatternProviderBlock> LIMITED_PATTERN_PROVIDER = block(
            "Limited Pattern Provider",
            "limited_pattern_provider",
            () -> new LimitedPatternProviderBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)),
            AEBaseBlockItem::new
    );

    private static <T extends Block> BlockDefinition<T> block(
            String englishName,
            String id,
            Supplier<T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        var block = BLOCKS.register(id, blockSupplier);
        var item = RegistryItems.ITEMS.register(id, () -> itemFactory.apply(block.get(), new Item.Properties()));
        RegistryItems.ITEMS_AR.add(item);
        return new BlockDefinition<>(englishName, block, new ItemDefinition<>(englishName, item));
    }
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

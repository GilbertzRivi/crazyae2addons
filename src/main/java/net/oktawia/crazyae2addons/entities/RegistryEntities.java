package net.oktawia.crazyae2addons.entities;

import net.neoforged.bus.api.IEventBus;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.oktawia.crazyae2addons.blocks.RegistryBlocks;

import java.util.function.Supplier;

public class RegistryEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CrazyAddons.MODID);

    public static final Supplier<BlockEntityType<CraftingCanceller>> CRAFTING_CANCELLER_ENTITY = BLOCK_ENTITY_TYPES.register(
            "crafting_canceller",
            () -> BlockEntityType.Builder.of(
                            CraftingCanceller::new,
                            RegistryBlocks.CRAFTING_CANCELLER.get()
                    )
                    .build(null)
    );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}

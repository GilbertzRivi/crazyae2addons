package net.oktawia.crazyae2addons;

import java.util.ArrayList;

import net.minecraft.network.chat.Component;
import net.oktawia.crazyae2addons.registries.RegistryItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;
import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.ItemDefinition;
import appeng.items.AEBaseItem;

public final class CACreativeTab {
    public static final DeferredRegister<CreativeModeTab> TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CrazyAddons.MODID);

    static {
        TAB.register("tab", () -> CreativeModeTab.builder()
                .title(Component.empty().append("Crazy AE Addons"))
                .icon(RegistryItems.RR_ITEM_P2P_TUNNEL::stack)
                .displayItems(CACreativeTab::populateTab)
                .build());
    }

    private static void populateTab(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        var itemDefs = new ArrayList<ItemDefinition<?>>();
        itemDefs.add(RegistryItems.RR_ITEM_P2P_TUNNEL);

        for (var itemDef : itemDefs) {
            var item = itemDef.asItem();

            // For block items, the block controls the creative tab
            if (item instanceof AEBaseBlockItem baseItem && baseItem.getBlock() instanceof AEBaseBlock baseBlock) {
                baseBlock.addToMainCreativeTab(params, output);
            } else if (item instanceof AEBaseItem baseItem) {
                baseItem.addToMainCreativeTab(params, output);
            } else {
                output.accept(itemDef);
            }
        }
    }
}
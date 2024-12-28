package net.oktawia.crazyae2addons.menus;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.blocks.CraftingCancellerBlock;

import java.util.function.Supplier;

public class RegistryMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, CrazyAddons.MODID);
    public static final Supplier<MenuType<CraftingCancellerMenu>> CRAFTING_CANCELLER = create(
            "crafting_canceller",
            CraftingCancellerMenu::new,
            CraftingCancellerBlock.class
    );

    private static <M extends AEBaseMenu, H> Supplier<MenuType<M>> create(
            String id, MenuTypeBuilder.MenuFactory<M, H> factory, Class<H> host) {
        return MENUS.register(id, () -> MenuTypeBuilder.create(factory, host).build(CrazyAddons.makeId(id)));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}

package net.oktawia.crazyae2addons.registries;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.entities.CraftingCanceller;
import net.oktawia.crazyae2addons.entities.LimitedPatternProvider;
import net.oktawia.crazyae2addons.logic.LimitedPatternProviderLogicHost;
import net.oktawia.crazyae2addons.menus.CraftingCancellerMenu;
import net.oktawia.crazyae2addons.menus.LimitedPatternProviderMenu;

import java.util.function.Supplier;

public class RegistryMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, CrazyAddons.MODID);
    public static final Supplier<MenuType<CraftingCancellerMenu>> CRAFTING_CANCELLER = create(
            "crafting_canceller",
            CraftingCancellerMenu::new,
            CraftingCanceller.class
    );

    public static final Supplier<MenuType<LimitedPatternProviderMenu>> LIMITED_PATTERN_PROVIDER = create(
            "limited_pattern_provider",
            LimitedPatternProviderMenu::new,
            LimitedPatternProviderLogicHost.class
    );

    private static <M extends AEBaseMenu, H> Supplier<MenuType<M>> create(
            String id, MenuTypeBuilder.MenuFactory<M, H> factory, Class<H> host) {
        return MENUS.register(id, () -> MenuTypeBuilder.create(factory, host).build(CrazyAddons.makeId(id)));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}

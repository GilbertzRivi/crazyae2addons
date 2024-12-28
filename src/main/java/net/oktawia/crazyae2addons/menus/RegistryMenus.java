package net.oktawia.crazyae2addons.menus;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.oktawia.crazyae2addons.CrazyAddons;

import java.util.function.Supplier;

public class RegistryMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, CrazyAddons.MODID);
    public static final Supplier<MenuType<CraftingCancellerMenu>> CRAFTING_CANCELLER = MENUS.register(
            "crafting_canceller",
            () -> new MenuType<>(
                    CraftingCancellerMenu::new,
                    FeatureFlags.DEFAULT_FLAGS
            )
    );
    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}

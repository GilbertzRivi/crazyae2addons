package net.oktawia.crazyae2addons.logic;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.stacks.AEItemKey;
import appeng.api.util.IConfigManager;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import appeng.api.util.IConfigurableObject;
import appeng.helpers.IPriorityHost;
import appeng.helpers.patternprovider.PatternContainer;
import net.oktawia.crazyae2addons.registries.RegistryMenus;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public interface LimitedPatternProviderLogicHost extends IConfigurableObject, IPriorityHost, PatternContainer {
    LimitedPatternProviderLogic getLogic();

    BlockEntity getBlockEntity();

    EnumSet<Direction> getTargets();

    void saveChanges();

    default int numberOfPatternSlots() {
        return getLogic().getPatternInv().size();
    }

    @Override
    default IConfigManager getConfigManager() {
        return getLogic().getConfigManager();
    }

    @Override
    default int getPriority() {
        return getLogic().getPriority();
    }

    @Override
    default void setPriority(int newValue) {
        getLogic().setPriority(newValue);
    }

    default void openMenu(Player player, MenuHostLocator locator) {
        MenuOpener.open(RegistryMenus.LIMITED_PATTERN_PROVIDER.get(), player, locator);
    }

    @Override
    default void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(RegistryMenus.LIMITED_PATTERN_PROVIDER.get(), player, subMenu.getLocator());
    }

    @Override
    default @Nullable IGrid getGrid() {
        return getLogic().getGrid();
    }

    AEItemKey getTerminalIcon();

    @Override
    default boolean isVisibleInTerminal() {
        return getLogic().getConfigManager().getSetting(Settings.PATTERN_ACCESS_TERMINAL) == YesNo.YES;
    }

    @Override
    default InternalInventory getTerminalPatternInventory() {
        return getLogic().getPatternInv();
    }

    @Override
    default long getTerminalSortOrder() {
        return getLogic().getSortValue();
    }

    default PatternContainerGroup getTerminalGroup() {
        return getLogic().getTerminalGroup();
    }
}
package net.oktawia.crazyae2addons.menus;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.oktawia.crazyae2addons.menus.RegistryMenus;

public class CraftingCancellerMenu extends AbstractContainerMenu {

    public CraftingCancellerMenu(int containerId, Inventory playerInv) {
        super(RegistryMenus.CRAFTING_CANCELLER.get(), containerId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }
}

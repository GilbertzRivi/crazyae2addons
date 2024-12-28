package net.oktawia.crazyae2addons.menus;

import appeng.menu.implementations.UpgradeableMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.oktawia.crazyae2addons.blocks.CraftingCancellerBlock;

public class CraftingCancellerMenu extends UpgradeableMenu<CraftingCancellerBlock> {

    public CraftingCancellerMenu(int id, Inventory ip, CraftingCancellerBlock host) {
        this(RegistryMenus.CRAFTING_CANCELLER.get(), id, ip, host);
    }

    public CraftingCancellerMenu(
            MenuType<? extends CraftingCancellerMenu> menuType, int id, Inventory ip, CraftingCancellerBlock host) {
        super(menuType, id, ip, host);
    }
}

package net.oktawia.crazyae2addons.screens;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.menus.LimitedPatternProviderMenu;

public class LimitedPatternProviderScreen extends AEBaseScreen<LimitedPatternProviderMenu> {
    public LimitedPatternProviderScreen(
            LimitedPatternProviderMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}

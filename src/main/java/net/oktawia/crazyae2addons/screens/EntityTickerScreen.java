package net.oktawia.crazyae2addons.screens;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.Utils;
import net.oktawia.crazyae2addons.entities.EntityTicker;
import net.oktawia.crazyae2addons.menus.EntityTickerMenu;

import static java.lang.Math.pow;

public class EntityTickerScreen extends UpgradeableScreen<EntityTickerMenu> {
    public int upgradeNum = 0;
    @Override
    protected void updateBeforeRender(){
        super.updateBeforeRender();
        double powerUsage =  1024 * pow(4, EntityTicker.energyUsageScaleValue * upgradeNum);
        setTextContent("energy", Component.empty().append(String.format("Energy Usage: %s FE/t", Utils.shortenNumber(powerUsage))));
        setTextContent("speed", Component.empty().append(String.format("Current multiplier: %d", (int) pow(2, upgradeNum))));
    }

    public EntityTickerScreen(
            EntityTickerMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    public void refreshGui(int num){
        this.upgradeNum = num;
        double powerUsage = 1024 * pow(4, EntityTicker.energyUsageScaleValue * upgradeNum);
        setTextContent("energy", Component.empty().append(String.format("Energy Usage: %s FE/t", Utils.shortenNumber(powerUsage))));
        setTextContent("speed", Component.empty().append(String.format("Current multiplier: %d", (int) pow(2, num))));
    }
}


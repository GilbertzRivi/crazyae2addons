package net.oktawia.crazyae2addons.menus;

import appeng.menu.implementations.UpgradeableMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.oktawia.crazyae2addons.entities.CraftingCanceller;

public class CraftingCancellerMenu extends UpgradeableMenu<CraftingCanceller> {
    private static final String ACTION_SEND_STATE = "ActionSendState";
    private static final String ACTION_SEND_DURATION = "ActionSendDuration";
    public CraftingCancellerMenu(int id, Inventory ip, CraftingCanceller host) {
        this(RegistryMenus.CRAFTING_CANCELLER.get(), id, ip, host);
        registerClientAction(ACTION_SEND_STATE, Boolean.class, this::sendState);
        registerClientAction(ACTION_SEND_DURATION, Integer.class, this::sendDuration);
    }

    public CraftingCancellerMenu(
            MenuType<? extends CraftingCancellerMenu> menuType, int id, Inventory ip, CraftingCanceller host) {
        super(menuType, id, ip, host);
    }

    public void sendState(boolean state){
        if (isClientSide()){
            sendClientAction(ACTION_SEND_STATE, state);
            return;
        }
        else{
            this.getHost().setEnabled(state);
        }
    }

    public void sendDuration(int duration){
        if (isClientSide()){
            sendClientAction(ACTION_SEND_DURATION, duration);
            return;
        }
        else{
            this.getHost().setDuration(duration);
        }
    }
}

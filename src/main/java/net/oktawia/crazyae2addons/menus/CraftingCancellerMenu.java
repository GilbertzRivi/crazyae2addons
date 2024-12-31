package net.oktawia.crazyae2addons.menus;

import appeng.menu.implementations.UpgradeableMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.oktawia.crazyae2addons.entities.CraftingCanceller;
import net.oktawia.crazyae2addons.packets.GuiUpdatePacket;
import net.oktawia.crazyae2addons.registries.RegistryMenus;

public class CraftingCancellerMenu extends UpgradeableMenu<CraftingCanceller> {
    public Boolean en = false;
    public Integer dur = 0;
    private static final String ACTION_SEND_STATE = "ActionSendState";
    private static final String ACTION_SEND_DURATION = "ActionSendDuration";
    public CraftingCancellerMenu(int id, Inventory ip, CraftingCanceller host) {
        this(RegistryMenus.CRAFTING_CANCELLER.get(), id, ip, host);
        registerClientAction(ACTION_SEND_STATE, Boolean.class, this::sendState);
        registerClientAction(ACTION_SEND_DURATION, Integer.class, this::sendDuration);
        setGuiState(this.getHost().getEnabled(), this.getHost().getDuration());
    }

    public CraftingCancellerMenu(
            MenuType<? extends CraftingCancellerMenu> menuType, int id, Inventory ip, CraftingCanceller host) {
        super(menuType, id, ip, host);
    }

    public void sendState(boolean state){
        if (isClientSide()){
            sendClientAction(ACTION_SEND_STATE, state);
        }
        else{
            this.getHost().setEnabled(state);
            setGuiState(state, this.getHost().getDuration());
        }
    }

    public void sendDuration(int duration){
        if (isClientSide()){
            sendClientAction(ACTION_SEND_DURATION, duration);
        }
        else{
            this.getHost().setDuration(duration);
            setGuiState(this.getHost().getEnabled(), duration);
        }
    }

    @Override
    public void broadcastChanges(){
        super.broadcastChanges();
        if (isServerSide()){
            sendPacketToClient(new GuiUpdatePacket(this.en, this.dur));
        }
    }

    public void setGuiState(boolean en, int dur){
        this.en = en;
        this.dur = dur;
        broadcastChanges();
    }
}

package net.oktawia.crazyae2addons.menus;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.oktawia.crazyae2addons.entities.EntityTicker;
import net.oktawia.crazyae2addons.packets.CraftingCancellerPacket;
import net.oktawia.crazyae2addons.packets.EntityTickerPacket;
import net.oktawia.crazyae2addons.registries.RegistryMenus;

public class EntityTickerMenu extends AEBaseMenu {

    public Integer tickerStrength = 0;
    public Boolean tickerEnabled = false;
    private final EntityTicker host;
    private static final String ACTION_SEND_STATE = "ActionSendState";
    private static final String ACTION_SEND_DURATION = "ActionSendDuration";

    public EntityTickerMenu(
            int id, Inventory ip, EntityTicker host) {
        super(RegistryMenus.ENTITY_TICKER.get(), id, ip, host);
        this.host = host;
        tickerStrength = host.tickerStrength;
        tickerEnabled = host.tickerEnabled;
        registerClientAction(ACTION_SEND_STATE, Boolean.class, this::sendState);
        registerClientAction(ACTION_SEND_DURATION, Integer.class, this::sendStrength);
    }

    public static final MenuType<EntityTickerMenu> MENU_TYPE = MenuTypeBuilder
            .create(EntityTickerMenu::new, EntityTicker.class)
            .build("entity_ticker");

    public void sendState(boolean state){
        if (isClientSide()){
            sendClientAction(ACTION_SEND_STATE, state);
        }
        else{
            this.host.tickerEnabled = state;
            setGuiState(state, this.host.tickerStrength);
        }
    }

    public void sendStrength(int str){
        if (isClientSide()){
            sendClientAction(ACTION_SEND_DURATION, str);
        }
        else{
            this.host.tickerStrength = str;
            setGuiState(this.host.tickerEnabled, str);
        }
    }

    @Override
    public void broadcastChanges(){
        super.broadcastChanges();
        if (isServerSide()){
            sendPacketToClient(new EntityTickerPacket(this.tickerEnabled, this.tickerStrength));
        }
    }

    public void setGuiState(boolean en, int dur){
        this.tickerEnabled = en;
        this.tickerStrength = dur;
        broadcastChanges();
    }
}

package net.oktawia.crazyae2addons.menus;

import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.OptionalFakeSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.oktawia.crazyae2addons.entities.EntityTicker;
import net.oktawia.crazyae2addons.packets.EntityTickerPacket;
import net.oktawia.crazyae2addons.registries.RegistryMenus;

public class EntityTickerMenu extends UpgradeableMenu<EntityTicker> {
    public int upgradeNum = 0;
    private String ACTION_SEND_UPGRADE_NUM = "actionSendUpgradeNum";

    public EntityTickerMenu(
            int id, Inventory ip, EntityTicker host) {
        super(RegistryMenus.ENTITY_TICKER.get(), id, ip, host);
        getHost().menu = this;
        registerClientAction(ACTION_SEND_UPGRADE_NUM, Integer.class, this::sendUpgradeNum);
    }

    public void sendUpgradeNum(int num){
        if (isClientSide()){
            sendClientAction(ACTION_SEND_UPGRADE_NUM, num);
        }
        else{
            this.upgradeNum = num;
            broadcastChanges();
        }
    }

    @Override
    public void broadcastChanges(){
        if (isServerSide()){
            sendPacketToClient(new EntityTickerPacket(this.upgradeNum));
        }

        for (Object o : this.slots) {
            if (o instanceof OptionalFakeSlot fs) {
                if (!fs.isSlotEnabled() && !fs.getDisplayStack().isEmpty()) {
                    fs.clearStack();
                }
            }
        }

        this.standardDetectAndSendChanges();
    }
}

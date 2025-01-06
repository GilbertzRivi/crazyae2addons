package net.oktawia.crazyae2addons.menus;

import appeng.api.storage.ISubMenuHost;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.locator.MenuHostLocator;
import appeng.menu.locator.MenuLocators;
import appeng.menu.slot.FakeSlot;
import appeng.menu.slot.OptionalRestrictedInputSlot;
import appeng.menu.slot.RestrictedInputSlot;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.NetworkPayloadSetup;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.entities.NBTExportBus;
import net.oktawia.crazyae2addons.packets.NBTExportBusPacket;
import net.oktawia.crazyae2addons.registries.RegistryItems;
import net.oktawia.crazyae2addons.registries.RegistryMenus;
import net.oktawia.crazyae2addons.screens.NBTExportBusScreen;
import org.slf4j.Logger;

public class NBTExportBusMenu extends UpgradeableMenu<NBTExportBus> {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String SYNC_COMPONENTS = "actionSyncComponents";
    private static final String OPEN_SUBMENU = "actionOpenSubmenu";
    public NBTExportBusScreen screen = null;
    public NBTExportBusMenu(int id, Inventory ip, NBTExportBus host) {
        super(RegistryMenus.NBT_EXPORT_BUS.get(), id, ip, host);
        registerClientAction(SYNC_COMPONENTS, this::syncComponents);
        registerClientAction(OPEN_SUBMENU, this::openSubmenu);
    }

    public void syncComponents(){
        if(isClientSide()){
            PacketDistributor.sendToServer(new NBTExportBusPacket(getHost().matchMode, getHost().components));
        }
    }

    @Override
    protected void setupConfig() {
        this.addSlot(new FakeSlot(this.getHost().inventory, 0), SlotSemantics.CONFIG);
    }

    public void openSubmenu() {
        if(isClientSide()){
            sendClientAction(OPEN_SUBMENU);
        }
        else {
            MenuOpener.open(RegistryMenus.NBT_LIST_SUBMENU.get(), getPlayer(), MenuLocators.forPart(getHost()));
        }
    }
}

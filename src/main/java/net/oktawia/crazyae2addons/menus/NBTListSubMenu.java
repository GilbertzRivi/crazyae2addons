package net.oktawia.crazyae2addons.menus;

import appeng.api.storage.ISubMenuHost;
import appeng.helpers.IPriorityHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.PriorityMenu;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.locator.MenuLocators;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.PacketDistributor;
import net.oktawia.crazyae2addons.entities.NBTExportBus;
import net.oktawia.crazyae2addons.packets.NBTRemoveComponentPacket;
import net.oktawia.crazyae2addons.registries.RegistryMenus;
import net.oktawia.crazyae2addons.screens.NBTListSubScreen;

import java.awt.*;

public class NBTListSubMenu extends AEBaseMenu {
    public NBTListSubScreen screen;
    private static final String ACTION_MAINMENU = "ActionMainMenu";
    public NBTExportBus host;
    public NBTListSubMenu(int id, Inventory playerInventory, NBTExportBus host) {
        super(RegistryMenus.NBT_LIST_SUBMENU.get(), id, playerInventory, host);
        this.host = host;
        registerClientAction(ACTION_MAINMENU, this::mainMenuOpener);
    }

    public void mainMenuOpener() {
        if(isClientSide()){
            sendClientAction(ACTION_MAINMENU);
        }
        else{
            MenuOpener.open(RegistryMenus.NBT_EXPORT_BUS.get(), getPlayer(), MenuLocators.forPart(host));
        }
    }

    public void removeComponent(TypedDataComponent<?> component) {
        if (isClientSide()){
            PacketDistributor.sendToServer(new NBTRemoveComponentPacket(component));
        }
    }
}

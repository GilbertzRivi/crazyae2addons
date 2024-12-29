package net.oktawia.crazyae2addons.screens;

import com.mojang.logging.LogUtils;
import net.oktawia.crazyae2addons.Utils;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.entities.CraftingCanceller;
import net.oktawia.crazyae2addons.menus.CraftingCancellerMenu;
import appeng.client.gui.widgets.AECheckbox;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.AE2Button;
import org.slf4j.Logger;


public class CraftingCancellerScreen extends UpgradeableScreen<CraftingCancellerMenu> {
    private static AETextField duration;
    private static AECheckbox onoffbutton;
    private static AE2Button confirm;
    private static boolean initialized = false;
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    protected void updateBeforeRender(){
        super.updateBeforeRender();
        if (!initialized){
            CraftingCanceller host = menu.getHost();
            onoffbutton.setSelected(host.getEnabled());
            duration.setValue(String.valueOf(host.getDuration()));
            initialized = true;
            LOGGER.info("Initialized screen");
        }
    }

    public CraftingCancellerScreen(
            CraftingCancellerMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        initialized = false;
        setupGui();
        this.widgets.add("onoffbutton", onoffbutton);
        this.widgets.add("duration", duration);
        this.widgets.add("confirm", confirm);
        LOGGER.info("class builder done");
    }

    private void setupGui(){
        onoffbutton = new AECheckbox(
                0, 0, 300, 10, style, Component.empty()
        );
        duration = new AETextField(
                style, Minecraft.getInstance().font, 0, 0, 0, 0
        );
        duration.setBordered(false);
        confirm = new AE2Button(
            0, 0, 0, 0, Component.empty().append("✔"), btn -> {
                validateInput();
            }
        );
    }

    private void validateInput(){
        String input = duration.getValue();
        boolean valid = true;
        try {
            int value = Integer.parseInt(input);
            if (value < 0 || value > 360) {
                valid = false;
            }
        } catch (NumberFormatException e) {
            valid = false;
        }
        boolean en = false;
        int dur = 0;
        if (!valid){
            LOGGER.info("invalid input");
            onoffbutton.setSelected(false);
            duration.setTextColor(0xFF0000);
            Runnable setColorFunction = () -> duration.setTextColor(0xFFFFFF);
            Runnable clearInput = () -> duration.setValue("");
            Utils.asyncDelay(setColorFunction, 1);
            Utils.asyncDelay(clearInput, 1);
        }
        else{
            en = true;
            dur = Integer.parseInt(duration.getValue());
            LOGGER.info("valid input");
            onoffbutton.setSelected(true);
            duration.setTextColor(0x00FF00);
            Runnable setColorFunction = () -> duration.setTextColor(0xFFFFFF);
            Runnable close = () -> this.getPlayer().closeContainer();
            Utils.asyncDelay(setColorFunction, 1);
            Utils.asyncDelay(close, 1.2f);
        }
        LOGGER.info(String.format("%b | %d", en, dur));
        menu.sendState(en);
        menu.sendDuration(dur);
    }
}

package net.oktawia.crazyae2addons.screens;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AE2Button;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.AECheckbox;
import appeng.menu.guisync.GuiSync;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.Utils;
import net.oktawia.crazyae2addons.menus.EntityTickerMenu;
import org.checkerframework.common.value.qual.StringVal;
import org.slf4j.Logger;

import static java.lang.Math.pow;

public class EntityTickerScreen extends AEBaseScreen<EntityTickerMenu> {
    private static AETextField value;
    private static AE2Button confirm;
    private static AE2Button add;
    private static AE2Button subtract;
    private static AECheckbox enabled;
    public static boolean initialized;
    private int tickerStrength = 0;
    private boolean tickerEnabled = false;
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    protected void updateBeforeRender(){
        super.updateBeforeRender();
        if (!initialized){
            if (Utils.inRange(tickerStrength, 10, 16)){
                value.setValue(String.format(" %d", tickerStrength));
            }
            else {
                value.setValue(String.format("%d", tickerStrength));
            }
            enabled.setSelected(tickerEnabled);
            int powerUsage = (int) (1024 * pow(tickerStrength, 1.5));
            initialized = true;
            setTextContent("energy", Component.empty().append(String.format("%d FE/t", powerUsage)));
        }
    }

    public EntityTickerScreen(
            EntityTickerMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        setupGui();
        this.widgets.add("value", value);
        this.widgets.add("confirm", confirm);
        this.widgets.add("add", add);
        this.widgets.add("subtract", subtract);
        this.widgets.add("enabled", enabled);
        initialized = false;
    }

    private void setupGui(){
        confirm = new AE2Button(
                0, 0, 0, 0, Component.empty().append("✔"), btn -> {
            validateInput();
        }
        );
        value = new AETextField(
                style, Minecraft.getInstance().font, 0, 0, 0, 0
        );
        value.setValue(String.valueOf(getMenu().tickerStrength));
        value.setBordered(false);
        add = new AE2Button(0, 0, 0, 0, Component.empty().append("+"), btn -> {
            buttonAction(1);
        });
        subtract = new AE2Button(0, 0, 0, 0, Component.empty().append("-"), btn -> {
            buttonAction(-1);
        });
        enabled = new AECheckbox(0, 0, 0, 0, style, Component.empty());
    }

    private void buttonAction(int val){
        if (Utils.checkNumber(value.getValue().strip())){
            int current = Integer.parseInt(value.getValue().strip());
            tickerStrength = current + val;
            if (tickerStrength > 16){
                tickerStrength = 16;
            } else if (tickerStrength < 0){
                tickerStrength = 0;
            }
            if (Utils.inRange(tickerStrength, 10, 16)){
                value.setValue(String.format(" %d", tickerStrength));
            }
            else {
                value.setValue(String.format("%d", tickerStrength));
            }
            int powerUsage = (int) (1024 * pow(tickerStrength, 1.5));
            setTextContent("energy", Component.empty().append(String.format("%d FE/t", powerUsage)));
        }
    }

    private void validateInput(){
        tickerEnabled = enabled.isSelected();
        String val = value.getValue().strip();
        boolean valid = Utils.checkNumber(val);
        int current = 0;
        if (valid && Utils.inRange(Integer.parseInt(value.getValue().strip()), 0, 16)) {
            valid = true;
            current = Integer.parseInt(value.getValue().strip());
        } else{
            valid = false;
        }
        if (!valid){
            tickerStrength = current;
            value.setTextColor(0xFF0000);
            Utils.asyncDelay(() -> value.setTextColor(0xFFFFFF), 1);
            Utils.asyncDelay(() -> value.setValue(""), 1);
        }
        else{
            tickerStrength = current;
            value.setTextColor(0x00FF00);
            Utils.asyncDelay(() -> value.setTextColor(0xFFFFFF), 1);
        }
        this.getMenu().sendStrength(tickerStrength);
        this.getMenu().sendState(tickerEnabled);
    }

    public void updateStatus(boolean en, int str){
        tickerStrength = str;
        tickerEnabled = en;
    }
}

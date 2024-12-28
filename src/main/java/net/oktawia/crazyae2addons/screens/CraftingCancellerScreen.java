package net.oktawia.crazyae2addons.screens;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.menus.CraftingCancellerMenu;


public class CraftingCancellerScreen extends UpgradeableScreen<CraftingCancellerMenu> {
    private Button masterButton;
    private EditBox delay;

    public CraftingCancellerScreen(
            CraftingCancellerMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    protected void init() {
        super.init();

        // Add a button
        myButton = this.addRenderableWidget(new Button(this.width / 2 - 50, this.height / 2 - 20, 100, 20, Component.literal("Click Me"), button -> {
            // Button action
            this.onButtonClick();
        }));

        // Add a text field
        myTextField = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 60, 200, 20, Component.literal("Input"));
        this.addRenderableWidget(myTextField);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // Render the background
        this.renderBackground(poseStack);

        // Render the text field and button
        super.render(poseStack, mouseX, mouseY, partialTicks);

        // Render custom elements, like text
        this.font.draw(poseStack, "My Custom GUI", this.width / 2 - 50, 10, 0xFFFFFF);
    }

    private void onButtonClick() {
        System.out.println("Button clicked! Text: " + myTextField.getValue());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (myTextField.isMouseOver(mouseX, mouseY)) {
            myTextField.setFocused(true);
        } else {
            myTextField.setFocused(false);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
}

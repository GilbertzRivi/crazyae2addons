package net.oktawia.crazyae2addons.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.oktawia.crazyae2addons.CrazyAddons;

public class CraftingCancellerScreen extends AbstractContainerScreen<AbstractContainerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrazyAddons.MODID, "textures/gui/img.png");

    public CraftingCancellerScreen(AbstractContainerMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.imageWidth = 176; // Width of the GUI
        this.imageHeight = 166; // Height of the GUI
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}

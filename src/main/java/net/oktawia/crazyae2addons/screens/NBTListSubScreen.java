package net.oktawia.crazyae2addons.screens;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AE2Button;
import appeng.client.gui.widgets.AETextField;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.model.renderable.IRenderable;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.menus.NBTListSubMenu;
import org.slf4j.Logger;

public class NBTListSubScreen extends AEBaseScreen<NBTListSubMenu> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public NBTListSubScreen(NBTListSubMenu menu, Inventory inventory, Component title, ScreenStyle style) {
        super(menu, inventory, title, style);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        int iter = 0;
        for (TypedDataComponent<?> component : getMenu().host.components){
            try {
                Codec<Object> codec = (Codec<Object>) component.type().codec();
                DynamicOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, getMenu().host.getLevel().registryAccess());
                DataResult<JsonElement> result = codec.encodeStart(ops, component.value());
                AE2Button btn = new AE2Button(
                        getGuiLeft() + 10,
                        iter*20+20,
                        getXSize()-20,
                        15,
                        Component.literal(String.format("%s: %s", component.type(), result.result().get())),
                        button -> {
                            renderables.remove(renderables.stream().filter(
                                    renderable -> {
                                        if (renderable instanceof AE2Button temp){
                                            return temp.getMessage().getString().equals(String.format("%s: %s", component.type(), result.result().get()));
                                        } else {
                                            return false;
                                        }
                                    }
                            ).findFirst().get());
                            getMenu().host.components.remove(component);
                            getMenu().removeComponent(component);
                        });
                if (this.renderables.stream().filter(
                        renderable -> {
                            if (renderable instanceof AE2Button temp){
                                return temp.getMessage().getString().equals(btn.getMessage().getString());
                            } else {
                                return false;
                            }
                        }
                ).toList().isEmpty()){
                    this.addRenderableWidget(btn);
                    iter ++;
                }
            } catch (Exception ignored) {}
        }
    }
}